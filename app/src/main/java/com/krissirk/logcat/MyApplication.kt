package com.krissirk.logcat

import android.app.Application
import android.util.Log
import com.krissirk.mylibrary.CrashReporter

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "MyApplication onCreate")
        CrashReporter.init(this)
        CrashReporter.loadCrash()
    }
}