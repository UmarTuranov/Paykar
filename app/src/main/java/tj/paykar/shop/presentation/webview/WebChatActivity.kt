package tj.paykar.shop.presentation.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import tj.paykar.shop.databinding.ActivityWebChatBinding

class WebChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityWebChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWebChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
       // clearSession(binding.webChat)
        setupWebView()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.apply {
            clouseButton.setOnClickListener {
                finish()
            }

            webChat.settings.javaScriptEnabled = true
            webChat.settings.domStorageEnabled = true
            webChat.settings.loadWithOverviewMode = true
            webChat.settings.useWideViewPort = true
            webChat.settings.builtInZoomControls = true
            webChat.settings.displayZoomControls = false
            webChat.settings.setSupportZoom(true)
            webChat.settings.defaultTextEncodingName = "utf-8"
            webChat.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url != null) {
                        view?.loadUrl(url)
                    }
                    return true
                }
            }
            webChat.loadUrl("https://paykar24.tj/online/shop")
        }
    }

    private fun clearSession(webView: WebView) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        webView.clearCache(true)
        webView.clearHistory()

        WebStorage.getInstance().deleteAllData()
    }
}