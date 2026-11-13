package com.pusca.keyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar

class PuscaKeyboardService : InputMethodService() {

    private val prefs by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }
    private var webView: WebView? = null
    private var loadedUrl: String? = null

    private fun px(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun appUrl(): String = prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateInputView(): View {
        val root = FrameLayout(this)
        val wv = WebView(this)
        wv.settings.javaScriptEnabled = true
        wv.settings.domStorageEnabled = true
        wv.settings.useWideViewPort = true
        wv.settings.loadWithOverviewMode = true
        wv.isVerticalScrollBarEnabled = false
        wv.setBackgroundColor(0xFFFFFFFF.toInt())

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)
        wv.addJavascriptInterface(Bridge(), "PuscaIME")

        val spinner = ProgressBar(this)
        root.addView(wv, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(300)))
        root.addView(spinner, FrameLayout.LayoutParams(px(28), px(28), Gravity.CENTER))

        wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                spinner.visibility = View.GONE
            }
        }

        loadedUrl = appUrl()
        wv.loadUrl(loadedUrl)
        webView = wv
        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (appUrl() != loadedUrl) {
            loadedUrl = appUrl()
            webView?.loadUrl(loadedUrl ?: DEFAULT_URL)
        }
    }

    private inner class Bridge {

        @JavascriptInterface
        fun commitText(text: String) {
            val ic = currentInputConnection ?: return
            ic.commitText(text, 1)
        }

        @JavascriptInterface
        fun deleteBackward() {
            val ic = currentInputConnection ?: return
            val selected = ic.getSelectedText(0)
            if (selected != null && selected.isNotEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        }

        @JavascriptInterface
        fun sendEnter() {
            val ic = currentInputConnection ?: return
            val options = currentInputEditorInfo?.imeOptions ?: 0
            val action = options and EditorInfo.IME_MASK_ACTION
            if (options and EditorInfo.IME_FLAG_NO_ENTER_ACTION != 0 || action == EditorInfo.IME_ACTION_NONE) {
                ic.commitText("\n", 1)
            } else {
                ic.performEditorAction(action)
            }
        }

        @JavascriptInterface
        fun setExpanded(expanded: Boolean) {
            val wv = webView ?: return
            wv.post {
                val lp = wv.layoutParams
                lp.height = px(if (expanded) 470 else 300)
                wv.layoutParams = lp
                wv.requestLayout()
            }
        }

        @JavascriptInterface
        fun hideKeyboard() {
            requestHideSelf(0)
        }
    }

    companion object {
        const val PREFS = "pusca_keyboard"
        const val KEY_URL = "app_url"
        const val DEFAULT_URL = "https://pusca-biz-flow.base44.app/keyboard-native"
    }
}
