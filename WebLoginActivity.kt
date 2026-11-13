package com.pusca.keyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView

class WebLoginActivity : Activity() {

    private var web: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PuscaKeyboardService.PREFS, MODE_PRIVATE)
        val url = prefs.getString(PuscaKeyboardService.KEY_URL, PuscaKeyboardService.DEFAULT_URL)
            ?: PuscaKeyboardService.DEFAULT_URL

        val wv = WebView(this)
        wv.settings.javaScriptEnabled = true
        wv.settings.domStorageEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)
        wv.loadUrl(url)

        setContentView(wv)
        web = wv
    }

    override fun onDestroy() {
        web?.destroy()
        super.onDestroy()
    }
}
