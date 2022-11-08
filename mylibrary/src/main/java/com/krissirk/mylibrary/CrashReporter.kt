package com.krissirk.mylibrary

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.ScreenUtils
import com.krissirk.mylibrary.data_store.DataStoreRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import java.util.*


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

    fun getDeviceInfo(): String {
        try {
            val deviceInfo = Pair("Device Information", "")
            val appVersionName = Pair("appVersionName", AppUtils.getAppVersionName().toString())
            val appVersionCode = Pair("appVersionCode", AppUtils.getAppVersionCode().toString())
            val sdkVersionName = Pair("sDKVersionName", DeviceUtils.getSDKVersionName().toString())
            val sdkVersionCode = Pair("sdkVersionCode", DeviceUtils.getSDKVersionCode().toString())
            val manufacturer = Pair("manufacturer", DeviceUtils.getManufacturer().toString())
            val architectures = Pair("architectures", DeviceUtils.getModel().toString())
            val isTablet = Pair("isTablet", DeviceUtils.isTablet().toString())
            val isSimulator = Pair("isSimulator", DeviceUtils.isEmulator().toString())
            val batteryPercentage = Pair("batteryPercentage", getBatteryPercentage().toString())
            val isCharging = Pair("isCharging", isCharging().toString())
            val orientation = Pair("orientation", getOrientation().toString())
            val screenWidth = Pair("screenWidth", ScreenUtils.getScreenWidth().toString())
            val screenHeight = Pair("screenHeight", ScreenUtils.getScreenHeight().toString())
            val screenDensity = Pair("screenDensity", ScreenUtils.getScreenDensity().toString())
            val screenDensityDpi =
                Pair("screenDensity", ScreenUtils.getScreenDensityDpi().toString())
            val isRooted = Pair("isRooted", DeviceUtils.isDeviceRooted().toString())
            val systemLanguage =
                Pair("systemLanguage", LanguageUtils.getSystemLanguage().toString())
            val applicationLanguage =
                Pair("applicationLanguage", LanguageUtils.getAppContextLanguage().toString())
            val availableMemory = Pair("availableMemory", getAvailableMemory().toString())
            val totalMemory = Pair("totalMemory", getTotalMemory().toString())
            val mutableList = mutableListOf<Pair<String, String>>()
            mutableList.add(deviceInfo)
            mutableList.add(appVersionName)
            mutableList.add(appVersionCode)
            mutableList.add(sdkVersionName)
            mutableList.add(sdkVersionCode)
            mutableList.add(manufacturer)
            mutableList.add(architectures)
            mutableList.add(isTablet)
            mutableList.add(isSimulator)
            mutableList.add(batteryPercentage)
            mutableList.add(isCharging)
            mutableList.add(orientation)
            mutableList.add(screenWidth)
            mutableList.add(screenHeight)
            mutableList.add(screenDensity)
            mutableList.add(screenDensityDpi)
            mutableList.add(isRooted)
            mutableList.add(systemLanguage)
            mutableList.add(applicationLanguage)
            mutableList.add(availableMemory)
            mutableList.add(totalMemory)
            var s = ""
            mutableList.forEach {
                s += "${it.first} : ${it.second}\n"
            }
            return s
        } catch (e: Exception) {
            return ""
        }
    }

    private fun getAvailableMemory(): String {
        try {
            val actManager =
                application.applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager

            // Declaring MemoryInfo object
            val memInfo = ActivityManager.MemoryInfo()

            // Fetching the data from the ActivityManager
            actManager.getMemoryInfo(memInfo)

            // Fetching the available and total memory and converting into Giga Bytes
            val availMemory = memInfo.availMem.toDouble() / (1024 * 1024 * 1024)
            return String.format("%.2f", availMemory) + "GiB"
        } catch (e: Exception) {
            return "-"
        }
    }

    private fun getTotalMemory(): String {
        try {
            val actManager =
                application.applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager

            // Declaring MemoryInfo object
            val memInfo = ActivityManager.MemoryInfo()

            // Fetching the data from the ActivityManager
            actManager.getMemoryInfo(memInfo)

            // Fetching the available and total memory and converting into Giga Bytes
            val totalMemory = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)
            return String.format("%.2f", totalMemory) + "GiB"
        } catch (e: Exception) {
            return "-"
        }
    }

    private fun getLocale(): String {
        val current: Locale = application.applicationContext.resources.configuration.locale
        return current.toString()
    }

    private fun getOrientation(): String {
        return when (application.applicationContext.resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                "Landscape"
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                "Portrait"
            }
            else -> {
                "Undefined"
            }
        }
    }

    private fun isCharging(): Boolean {
        try {
            val batteryStatus: Intent? =
                IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                    application.applicationContext.registerReceiver(null, ifilter)
                }
            // isCharging if true indicates charging is ongoing and vice-versa
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            return (status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL)
        } catch (e: Exception) {
            return false
        }
    }

    private fun getBatteryPercentage(): String {
        return try {
            val bm =
                application.applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
        } catch (e: Exception) {
            "-"
        }
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
                    Log.d("device info ", getDeviceInfo())
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