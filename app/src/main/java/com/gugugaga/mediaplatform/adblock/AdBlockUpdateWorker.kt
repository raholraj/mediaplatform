package com.gugugaga.mediaplatform.adblock

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gugugaga.mediaplatform.data.AppPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class AdBlockUpdateWorker @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    workerParams: WorkerParameters,
    private val prefs: AppPrefs,
) : CoroutineWorker(applicationContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            updateList()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun updateList() {
        val listUrl = prefs.adblockListUrl.takeIf { it.isNotBlank() } ?: return
        val conn = (URL(listUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", "MediaPlatform-AdBlock/1.0")
            prefs.adblockEtag?.let { setRequestProperty("If-None-Match", it) }
        }
        try {
            when (conn.responseCode) {
                304 -> return
                200 -> {
                    val tmp = File(applicationContext.cacheDir, "adblock_hosts.tmp")
                    var bytes = 0L
                    conn.inputStream.use { input -> tmp.outputStream().use { out ->
                        val buf = ByteArray(16_384)
                        while (true) {
                            val n = input.read(buf)
                            if (n < 0) break
                            bytes += n
                            if (bytes > 8_000_000) throw IllegalStateException("list too large")
                            out.write(buf, 0, n)
                        }
                    } }
                    // sanity check: a broken/empty list must never replace a working one
                    val lines = tmp.bufferedReader().useLines { ls -> ls.count { AdBlockEngine.parseLine(it) != null } }
                    if (lines < 100) { tmp.delete(); return }
                    val cur = File(applicationContext.filesDir, "adblock_hosts.txt")
                    if (cur.exists()) cur.copyTo(File(applicationContext.filesDir, "adblock_hosts.prev.txt"), overwrite = true)
                    tmp.copyTo(cur, overwrite = true)
                    tmp.delete()
                    conn.getHeaderField("ETag")?.let { prefs.adblockEtag = it }
                }
            }
        } finally {
            conn.disconnect()
        }
    }
}
