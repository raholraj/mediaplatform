package com.gugugaga.mediaplatform.util

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String, long: Boolean = false) {
    Toast.makeText(this, msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}
