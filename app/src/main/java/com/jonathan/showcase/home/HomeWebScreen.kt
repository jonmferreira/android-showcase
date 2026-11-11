package com.jonathan.showcase.home

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

// HOME = o STAGE WEB exato (showcase-interview/stage.html, verbatim + importmap), servido na raiz
// via WebViewAssetLoader pra o ESM do Three.js carregar sem CORS de file://. É o mesmo arquivo do
// site rodando no WebView — navegação própria do stage (setas/dots/teclas).
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeWebScreen(onOpen: (String) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(ctx))
                .build()
            WebView(ctx).apply {
                setBackgroundColor(android.graphics.Color.parseColor("#FF05070A"))
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClientCompat() {
                    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                        assetLoader.shouldInterceptRequest(request.url)
                }
                loadUrl("https://appassets.androidplatform.net/stage.html")
            }
        },
    )
}
