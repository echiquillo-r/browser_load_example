package com.example.browser_load_example

import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WebViewActivity : AppCompatActivity() {
    private var notSentYet = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl(intent.getStringExtra(EXTRA_URL_TO_USE)!!)
        myWebView.settings.javaScriptEnabled = true
        myWebView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d("WebView", "onConsoleMessage: ${consoleMessage?.messageLevel()} ${consoleMessage?.message()}")
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.d("WebView", "onProgressChanged: $newProgress")
                if (newProgress == 100 && notSentYet) {
                    SessionHolder.browserPageLoaded()
                    notSentYet = false
                }
            }
        }
    }

    companion object {
        const val EXTRA_URL_TO_USE = "extra_url_to_use"
    }
}