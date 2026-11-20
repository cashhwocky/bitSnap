# bitSnap is a themed WebView wrapper; no reflection-based code, defaults are enough.
# Keep the JS bridge callable from injected scripts.
-keepclassmembers class com.bitdroid.snap.** {
    @android.webkit.JavascriptInterface <methods>;
}
