package com.krissirk.logcat

import android.content.Context
import android.widget.Toast

class ShowToast {
    fun showToast(context: Context?, toast: String) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }
}