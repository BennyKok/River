package com.bennyv17.river.callback

import android.webkit.JavascriptInterface

class JsrsInterface(private val cb: JsrsCallback) {

    @JavascriptInterface
    fun reply(value: String) {
        cb.onReply(value)
    }
}

interface JsrsCallback {
    fun onReply(reply: String)
}
