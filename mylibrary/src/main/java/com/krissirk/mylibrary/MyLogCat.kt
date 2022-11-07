package com.krissirk.mylibrary

import android.content.Context
import android.widget.Toast

object MyLogCat {
    fun s(c: Context?, message: String?) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    }
}