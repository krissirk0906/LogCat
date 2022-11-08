package com.krissirk.mylibrary

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.krissirk.mylibrary.data_store.DataStoreRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest


//class Logger {
//    private val listLog: MutableList<String> = mutableListOf()
//    private var count = 0
//
//    companion object {
//        private var instance: Logger? = null
//        fun getInstance(): Logger {
//            if (instance == null)  // NOT thread safe!
//            {
//                instance = Logger()
//            }
//            return instance!!
//        }
//    }
//
//    fun saveLog(log: String) {
//        Log.d("saveLog", "saveLog")
//        count += 1
//        listLog.add(log)
//    }
//
//    fun showLog() {
//        Log.d("showLog ", count.toString())
//        Log.d("showLog ", listLog.size.toString())
//    }
//}
object CrashReporter {

    private lateinit var application: Application
    private val listLog: MutableList<String> = mutableListOf()

    fun init(app: Application) {
        application = app
    }

    fun saveLog(log: String) {
        listLog.add(log)
    }

    fun getActionLog(): String {
        var log = ""
        listLog.forEach {
            log += it + "\n"
        }
        return log
    }

    fun showLog() {
        Log.d("showLog ", listLog.toString())
    }

    suspend fun saveLogToLocalStorage() {
        DataStoreRepository(application.applicationContext).saveLogList(getActionLog())
    }

    suspend fun clearLogOfLocalStorage() {
        DataStoreRepository(application.applicationContext).saveLogList("")
    }

    fun saveLogLifeCycle(className: String, lifeCycle: String, time: Long) {
        val log =
            "${Utils.formatLogTime(time)} Lifecycle $lifeCycle at $className"
        listLog.add(log)
    }

    fun saveOtherLog(time: Long, logDetail: String) {
        val log =
            "${Utils.formatLogTime(time)} $logDetail"
        listLog.add(log)
    }

    fun loadCrash() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(
                "CoroutineExceptionHandler",
                "Caught $exception with suppressed ${exception.suppressed.contentToString()}"
            )
        }
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO + handler)
        {
            DataStoreRepository(application.applicationContext).readLogList().cancellable()
                .collectLatest {
                    Log.d("readLogList ", it)
                    if (it.isNotBlank()) {
                        reportBugToServer()
                    }
                }
        }
    }

    private fun reportBugToServer() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(
                "CoroutineExceptionHandler",
                "Caught $exception with suppressed ${exception.suppressed.contentToString()}"
            )
        }
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO + handler)
        {
            delay(3000L)
            clearLogOfLocalStorage()
            Log.d("11", "Sent crash report to server successfully")
            CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main + handler)
            {
                Toast.makeText(
                    application.applicationContext,
                    "Sent crash report to server successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun saveLogForView(
        action: String,
        viewId: String,
        viewClass: String,
        parentClass: String,
        time: Long
    ) {
        val log =
            "${Utils.formatLogTime(time)} $action $viewId type $viewClass at class $parentClass"
        listLog.add(log)
    }
}

enum class ActionEnum(val id: Int, val action: String) {
    CLICK(0, "Click"),
    INPUT(1, "Input"),
    SCROLL(2, "Scroll"),
    LONG_CLICK(3, "Long click")
}