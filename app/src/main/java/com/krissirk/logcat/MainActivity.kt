package com.krissirk.logcat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.krissirk.logcat.Extension.onClick
import com.krissirk.mylibrary.CrashReporter
import com.krissirk.mylibrary.TopExceptionHandler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).onClick(
            this
        ) { showSomeThing() }

        findViewById<Button>(R.id.button3).onClick(
            this
        ) { doCrash() }

        findViewById<Button>(R.id.button2).onClick(
            this
        ) { showLog() }
        Thread.setDefaultUncaughtExceptionHandler(TopExceptionHandler(this))
    }

    private fun doCrash() {
        val e = 4 + "s".toInt()
    }

    private fun showLog() {
        CrashReporter.showLog()
    }

    override fun onResume() {
        super.onResume()
        CrashReporter.saveLogLifeCycle(this.javaClass.name, "onResume", System.currentTimeMillis())
    }

    override fun onStop() {
        super.onStop()
        CrashReporter.saveLogLifeCycle(this.javaClass.name, "onResume", System.currentTimeMillis())
    }

    private fun showSomeThing() {
        Log.d("showSomeThing", "showSomeThing")
    }
}