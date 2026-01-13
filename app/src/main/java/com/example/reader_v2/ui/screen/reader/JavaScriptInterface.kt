package com.example.reader_v2.ui.screen.reader

import android.webkit.JavascriptInterface

class JavaScriptInterface(
    private val handleJsStateChange: (String) -> Unit,
    private val handleJsEdgeTap: (String) -> Unit,
) {
    @JavascriptInterface
    fun onStateChange(stateJson: String) {
        handleJsStateChange(stateJson)
    }

    @JavascriptInterface
    fun onEdgeTap(direction: String) {
        handleJsEdgeTap(direction)
    }

    @JavascriptInterface
    fun log(message: String) {
        android.util.Log.d("WebViewConsole", message)
    }
}
