package com.krissirk.logcat

import android.content.Context
import android.view.View
import com.krissirk.mylibrary.ActionEnum
import com.krissirk.mylibrary.CrashReporter

object Extension {
    fun View.onClick(context: Context, unit: () -> Unit) {
        setOnClickListener {
            CrashReporter.saveLogForView(
                ActionEnum.CLICK.action,
                this.resources.getResourceEntryName(this.id),
                this.javaClass.name,
                context.javaClass.name,
                System.currentTimeMillis()
            )
            unit.invoke()
        }
    }
}