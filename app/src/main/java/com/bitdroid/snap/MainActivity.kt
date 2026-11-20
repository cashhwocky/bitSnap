package com.bitdroid.snap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.bitdroid.snap.ui.Bit
import com.bitdroid.snap.ui.BitTheme
import com.bitdroid.snap.ui.ErrorScreen
import com.bitdroid.snap.ui.LoadingOverlay
import com.bitdroid.snap.ui.MoreSheet
import com.bitdroid.snap.ui.SnapTabBar
import com.bitdroid.snap.ui.backdrop

class MainActivity : ComponentActivity() {

    private lateinit var web: WebView

    // Compose-visible state.
    private var progress by mutableFloatStateOf(0f)
    private var loading by mutableStateOf(true)
    private var failed by mutableStateOf(false)
    private var selectedTab by mutableIntStateOf(0)
    private var showMore by mutableStateOf(false)

    // File upload (sending photos into Snapchat web).
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooser = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uris = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    // Camera / mic requests coming from the web client.
    private var pendingPermission: PermissionRequest? = null
    private val permLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        val req = pendingPermission
        pendingPermission = null
        if (req != null) {
            val allGranted = grants.values.all { it }
            if (allGranted) req.grant(req.resources) else req.deny()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Bit.apply(this)

        CookieManager.getInstance().setAcceptCookie(true)

        web = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            configure(this)
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)

        if (savedInstanceState != null) web.restoreState(savedInstanceState) else web.loadUrl(HOME)

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (showMore) { showMore = false; return }
                if (web.canGoBack()) web.goBack() else { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
            }
        })

        setContent {
            BitTheme {
                Box(Modifier.fillMaxSize().backdrop()) {
                    // The web client. Hidden behind the overlay until the first page settles.
                    AndroidView(factory = { web }, modifier = Modifier.fillMaxSize())

                    if (loading && !failed) LoadingOverlay(progress)
                    if (failed) ErrorScreen(onRetry = { failed = false; loading = true; web.reload() })

                    if (!loading && !failed) {
                        SnapTabBar(
                            selected = selectedTab,
                            onSelect = { i ->
                                when (i) {
                                    0 -> { selectedTab = 0; web.loadUrl(HOME) }
                                    1 -> { web.reload() }          // momentary
                                    2 -> { selectedTab = 2; showMore = true }
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }

                    if (showMore) {
                        MoreSheet(
                            onDismiss = { showMore = false; selectedTab = 0 },
                            onOpenInBrowser = {
                                showMore = false; selectedTab = 0
                                runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(web.url ?: HOME))) }
                            },
                            onLogout = {
                                showMore = false; selectedTab = 0
                                clearSession()
                                loading = true; failed = false
                                web.loadUrl(HOME)
                            },
                            onTheme = { key ->
                                com.bitdroid.snap.ui.Themes.set(this@MainActivity, key)
                            },
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configure(w: WebView) {
        w.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = true
            allowContentAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            // web.snapchat.com refuses mobile browsers, so present as desktop Chrome.
            userAgentString = DESKTOP_UA
        }
        WebView.setWebContentsDebuggingEnabled(false)

        w.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url
                val host = url.host ?: return false
                return if (isInternalHost(host)) {
                    false // let the web view handle Snapchat + login domains itself
                } else {
                    // Anything else (shared links, help pages) opens in the real browser.
                    runCatching { startActivity(Intent(Intent.ACTION_VIEW, url)) }.isSuccess
                }
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                failed = false
            }

            override fun onPageFinished(view: WebView, url: String?) {
                loading = false
                // Keep the page in dark mode so it sits on the Bloom backdrop; defensive, never fatal.
                view.evaluateJavascript(
                    "(function(){try{document.documentElement.style.colorScheme='dark';}catch(e){}})();",
                    null,
                )
                CookieManager.getInstance().flush()
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) { failed = true; loading = false }
            }
        }

        w.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress = newProgress / 100f
                if (newProgress >= 100) loading = false
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                val needed = mutableListOf<String>()
                request.resources.forEach { res ->
                    when (res) {
                        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> needed += Manifest.permission.CAMERA
                        PermissionRequest.RESOURCE_AUDIO_CAPTURE -> needed += Manifest.permission.RECORD_AUDIO
                    }
                }
                val missing = needed.filter {
                    ContextCompat.checkSelfPermission(this@MainActivity, it) != PackageManager.PERMISSION_GRANTED
                }
                if (missing.isEmpty()) {
                    request.grant(request.resources)
                } else {
                    pendingPermission = request
                    permLauncher.launch(missing.toTypedArray())
                }
            }

            override fun onShowFileChooser(
                webView: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams,
            ): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                val intent = params.createIntent()
                return runCatching { fileChooser.launch(intent); true }
                    .getOrElse { filePathCallback = null; false }
            }

            // "Log in with Google / Apple" and other popups open in a themed dialog window.
            override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message): Boolean {
                val popup = WebView(this@MainActivity).apply { configurePopup(this) }
                val dialog = Dialog(this@MainActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    val container = FrameLayout(this@MainActivity).apply {
                        setBackgroundColor(android.graphics.Color.parseColor("#120F1C"))
                        addView(popup, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    }
                    setContentView(container)
                }
                popup.webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView) { runCatching { dialog.dismiss() } }
                }
                dialog.setOnDismissListener { runCatching { popup.destroy() } }
                dialog.show()
                (resultMsg.obj as WebView.WebViewTransport).webView = popup
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurePopup(w: WebView) {
        w.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            userAgentString = DESKTOP_UA
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(w, true)
        w.webViewClient = WebViewClient()
    }

    private fun clearSession() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        web.clearCache(true)
        web.clearHistory()
        android.webkit.WebStorage.getInstance().deleteAllData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web.saveState(outState)
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }

    private fun isInternalHost(host: String): Boolean =
        INTERNAL_HOSTS.any { host == it || host.endsWith(".$it") }

    companion object {
        const val HOME = "https://web.snapchat.com/"
        const val DESKTOP_UA =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"
        val INTERNAL_HOSTS = listOf(
            "snapchat.com", "snap.com", "sc-cdn.net", "sc-corp.net",
            "accounts.google.com", "google.com", "gstatic.com",
            "appleid.apple.com", "apple.com",
        )
    }
}
