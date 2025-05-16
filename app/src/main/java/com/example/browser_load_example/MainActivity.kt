package com.example.browser_load_example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.browser_load_example.google_sample.CustomTabActivityHelper
import com.example.browser_load_example.google_sample.CustomTabsHelper


class MainActivity : CustomTabActivityHelper.ConnectionCallback, AppCompatActivity() {
    private var customTabActivityHelper: CustomTabActivityHelper? = null
    private var connectedIndicator: View? = null
    private var url: String = "https://www.redditforbusiness.com/advertise/ad-types/mobile-ads-on-reddit/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent.getStringExtra(EXTRA_URL_TO_USE)?.let {
            url = it
        } ?: run {
            Log.d("MainActivity", "No URL provided, using default URL")
        }

        // always warmup for now
        val shouldWarmup = intent.getBooleanExtra(
            EXTRA_SHOULD_WARMUP,
            true
        )
        customTabActivityHelper = CustomTabActivityHelper(shouldWarmup).also {
            it.setConnectionCallback(this)
        }

        connectedIndicator = findViewById(R.id.connected_indicator)


        findViewById<Button>(R.id.open_web_view).setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.EXTRA_URL_TO_USE, url)
            SessionHolder.startTimer()
            startActivity(intent)
        }
        findViewById<Button>(R.id.open_chrome_custom_tab).setOnClickListener {
            SessionHolder.startTimer()
            openCustomTab(url)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        customTabActivityHelper?.setConnectionCallback(null)
    }

    override fun onCustomTabsConnected() {
        connectedIndicator?.visibility = View.VISIBLE
        // upon connection check if should prewarm url
        if (intent.getBooleanExtra(EXTRA_SHOULD_PREWARM_URL, true)) {
            customTabActivityHelper?.mayLaunchUrl(url.toUri(), null, null, SessionHolder.mCustomTabsCallback).also {
                Log.d("MainActivity", "mayLaunchUrl was successful: $it")
            }
        }
    }

    override fun onCustomTabsDisconnected() {
        connectedIndicator?.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
        customTabActivityHelper?.bindCustomTabsService(this)
    }

    override fun onStop() {
        Log.d("MainActivity", "onStop")
        customTabActivityHelper?.unbindCustomTabsService(this)
        super.onStop()
    }

    private fun openCustomTab(url: String) {
        val uri = url.toUri()
        val customTabsIntent =
            CustomTabsIntent.Builder(customTabActivityHelper?.getSession(SessionHolder.mCustomTabsCallback))
                .build()
        CustomTabActivityHelper.openCustomTab(
            this, customTabsIntent, uri, NoOpWebviewFallback()
        )
    }

    companion object {
        const val EXTRA_URL_TO_USE = "extra_url_to_use"
        const val EXTRA_SHOULD_WARMUP = "extra_should_warmup"
        const val EXTRA_SHOULD_PREWARM_URL = "extra_should_prewarm_url"
    }
}