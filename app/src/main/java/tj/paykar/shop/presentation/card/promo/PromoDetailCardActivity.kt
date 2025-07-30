package tj.paykar.shop.presentation.card.promo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityPromoDetailCardBinding

class PromoDetailCardActivity : AppCompatActivity() {

    lateinit var binding: ActivityPromoDetailCardBinding

    private var title: String = ""
    private var period: String = ""
    private var descriptionText: String = ""
    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromoDetailCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                Glide.with(this@PromoDetailCardActivity)
                    .load("https://paykar.tj$image")
                    .fitCenter()
                    .placeholder(R.drawable.product_image)
                    .into(promoImage)
            }
        }
    }
}