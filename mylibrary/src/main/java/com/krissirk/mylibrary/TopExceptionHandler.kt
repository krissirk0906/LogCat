package com.krissirk.mylibrary

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException


class TopExceptionHandler(app: Activity?) : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler =
        Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler
    private var app: Activity? = null

    init {
        this.app = app
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.d("uncaughtException ", e.toString())
        Toast.makeText(
            app,
            "Sorry, the application crashed. A report will be sent to the developers.",
            Toast.LENGTH_SHORT
        ).show()
        var arr = e.stackTrace
        var report = """
            $e
            
            
            """.trimIndent()
        report += "--------- Stack trace ---------\n\n"
        for (i in arr.indices) {
            report += """    ${arr[i]}
"""
        }
        report += "-------------------------------\n\n"

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n"
        val cause = e.cause
        if (cause != null) {
            report += """
                $cause
                
                
                """.trimIndent()
            arr = cause.stackTrace
            for (i in arr.indices) {
                report += """    ${arr[i]}
"""
            }
        }
        report += "-------------------------------\n\n"
//        try {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(
                "CoroutineExceptionHandler",
                "Caught $exception with suppressed ${exception.suppressed.contentToString()}"
            )
        }
        CrashReporter.saveLog(report)
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO + handler) {
            CrashReporter.saveLogToLocalStorage()
        }
//            val trace: FileOutputStream = app!!.openFileOutput(
//                "stack.trace",
//                Context.MODE_PRIVATE
//            )
//            trace.write(report.toByteArray())
//            trace.close()
//        } catch (ioe: IOException) {
//            // ...
//        }
        defaultUEH.uncaughtException(t, e)
    }
}