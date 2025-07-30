package tj.paykar.shop.presentation.shop.promo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityPromoDetailBinding
import tj.paykar.shop.presentation.webview.WebViewActivity

class PromoDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityPromoDetailBinding

    private var title: String = ""
    private var period: String = ""
    private var descriptionText: String = ""
    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = this.resources.getColor(R.color.whiteToBlack)
        getPutIntent()
        setupView()
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        title = bundle?.getString("title") ?: ""
        period = bundle?.getString("period") ?: ""
        descriptionText = bundle?.getString("description") ?: ""
        image = bundle?.getString("image") ?: ""
    }

    private fun setupView() {
        binding.apply {
            titlePromo.text = title
            periodPromo.text = period
            description.text = descriptionText
            CoroutineScope(Dispatchers.Main).launch {
                Glide.with(this@PromoDetailActivity)
                    .load("https://paykar.shop$image")
                    .fitCenter()
                    .placeholder(R.drawable.product_image)
                    .into(promoImage)
            }

            webView.setOnClickListener {
                val intent = Intent(this@PromoDetailActivity, WebViewActivity::class.java)
                intent.putExtra("webTitle", "Акции")
                intent.putExtra("webUrl", "https://paykar.shop/sale/")
                startActivity(intent)
            }
        }
    }
}