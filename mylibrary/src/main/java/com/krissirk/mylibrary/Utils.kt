package com.krissirk.mylibrary

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    @SuppressLint("SimpleDateFormat")
    fun formatLogTime(millisecond: Long): String {
        return try {
            val simple: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS")
            val result = Date(millisecond)
            simple.format(result)
        } catch (e: Exception) {
            ""
        }
    }
}