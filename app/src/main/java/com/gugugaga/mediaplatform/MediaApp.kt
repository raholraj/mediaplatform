package com.gugugaga.mediaplatform

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.database.FirebaseDatabase
import com.gugugaga.mediaplatform.adblock.AdBlockUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MediaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Keep the last crash on disk so it can be read in Settings (no logcat needed when building via Actions).
        val prev = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            runCatching {
                java.io.File(filesDir, "last_crash.txt").writeText(
                    java.util.Date().toString() + "\nThread: " + t.name + "\n" + android.util.Log.getStackTraceString(e),
                )
            }
            prev?.uncaughtException(t, e)
        }
        // Must run before any other RTDB use; ignore if already initialised.
        runCatching { FirebaseDatabase.getInstance().setPersistenceEnabled(true) }
        runCatching {
            val req = PeriodicWorkRequestBuilder<AdBlockUpdateWorker>(1, TimeUnit.DAYS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork("adblock_update", ExistingPeriodicWorkPolicy.KEEP, req)
            val now = OneTimeWorkRequestBuilder<AdBlockUpdateWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(this).enqueueUniqueWork("adblock_update_now", ExistingWorkPolicy.KEEP, now)
        }
    }
}
