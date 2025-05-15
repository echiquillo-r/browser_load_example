package com.example.browser_load_example

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.example.browser_load_example.google_sample.CustomTabActivityHelper.CustomTabFallback

class NoOpWebviewFallback: CustomTabFallback {
    override fun openUri(activity: Activity?, uri: Uri?) {
        Toast.makeText(activity, "No WebView available", Toast.LENGTH_SHORT).show()
    }
}