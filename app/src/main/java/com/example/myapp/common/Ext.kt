package com.example.myapp.common

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes

const val CHECK_LOG = "CHECK_LOG"
fun log(msg: String) {
    Log.d(CHECK_LOG, msg)
}

fun getAppString(
    @StringRes stringId: Int,
    context: Context? = getApplication()
): String {
    return context?.getString(stringId) ?: ""
}