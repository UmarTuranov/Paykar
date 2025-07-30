package tj.paykar.shop.presentation.card.promo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityPromoCardBinding
import tj.paykar.shop.domain.adapter.card.PromoCardAdaper

class PromoCardActivity : AppCompatActivity() {

    lateinit var binding: ActivityPromoCardBinding
    val adapter = PromoCardAdaper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPromoCardBinding.inflate(layoutInflater)
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
        setupAdapter()
        service()
    }

    private fun setupAdapter() {
        binding.apply {
            promoCard.setHasFixedSize(true)
            promoCard.layoutManager = LinearLayoutManager(this@PromoCardActivity, LinearLayoutManager.VERTICAL, false)
            promoCard.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                adapter.sendRequest()
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@PromoCardActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@PromoCardActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }
}