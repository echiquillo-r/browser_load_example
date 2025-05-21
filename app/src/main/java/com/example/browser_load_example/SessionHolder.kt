package com.example.browser_load_example

import android.content.ServiceConnection
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsSession


object SessionHolder {
    var currentClickTime = 0L
    var timings: MutableList<Long> = mutableListOf()
    val mCustomTabsCallback: CustomTabsCallback = object : CustomTabsCallback() {
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            val event = when (navigationEvent) {
                NAVIGATION_ABORTED -> "NAVIGATION_ABORTED"
                NAVIGATION_FAILED -> "NAVIGATION_FAILED"
                NAVIGATION_FINISHED -> "NAVIGATION_FINISHED"
                NAVIGATION_STARTED -> "NAVIGATION_STARTED"
                TAB_SHOWN -> "TAB_SHOWN"
                TAB_HIDDEN -> "TAB_HIDDEN"
                else -> navigationEvent.toString()
            }
            if (navigationEvent == NAVIGATION_FINISHED) {
                browserPageLoaded()
            }
            System.out.println("ChromeCustomTab: onNavigationEvent: $event")
        }
    }
    fun startTimer() {
        currentClickTime = System.currentTimeMillis()
    }
    fun browserPageLoaded() {
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - currentClickTime
        Log.d("SessionHolder", "Time difference: $timeDiff ms")
        if (timeDiff > 1000) {
            Log.d("SessionHolder", "Page loaded after more than 1 second")
        } else {
            Log.d("SessionHolder", "Page loaded within 1 second")
        }
        timings.add(timeDiff)
    }
}