package com.example.browser_load_example

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
        customTabActivityHelper = CustomTabActivityHelper(true).also {
            it.setConnectionCallback(this)
        }

        connectedIndicator = findViewById(R.id.connected_indicator)


        findViewById<Button>(R.id.open_web_view).setOnClickListener {
           // todo
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
        // upon connection prewarm url
        customTabActivityHelper?.mayLaunchUrl(url.toUri(), null, null).also {
            Log.d("MainActivity", "mayLaunchUrl was successful: $it")
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
            CustomTabsIntent.Builder(customTabActivityHelper!!.session)
                .build()
        CustomTabActivityHelper.openCustomTab(
            this, customTabsIntent, uri, NoOpWebviewFallback()
        )
    }

    companion object {
        const val EXTRA_URL_TO_USE = "extra_url_to_use"
    }
}