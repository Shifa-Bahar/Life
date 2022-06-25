package com.lifepharmacy.application.utils.universal

import android.util.Log

object Logger {
    private var isDebugable = true
    fun e(tag: String, message: String) {
        if (isDebugable) {
            Log.e(tag, message + "")
        }
    }

    fun d(tag: String, message: String) {
        if (isDebugable) {
            Log.d(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (isDebugable) {
            Log.i(tag, message)
        }
    }

}