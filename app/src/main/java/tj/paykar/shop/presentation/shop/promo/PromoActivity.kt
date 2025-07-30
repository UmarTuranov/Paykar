package tj.paykar.shop.presentation.shop.promo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import tj.paykar.shop.databinding.ActivityPromoBinding
import tj.paykar.shop.domain.adapter.promo.PromoAdapter
import tj.paykar.shop.presentation.webview.WebChatActivity

class PromoActivity : AppCompatActivity() {

    lateinit var binding: ActivityPromoBinding
    val adapter = PromoAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPromoBinding.inflate(layoutInflater)
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
        binding.chatButton.setOnClickListener {
            val intent = Intent(this, WebChatActivity::class.java)
            startActivity(intent)
        }
        setupAdapter()
        service()
    }

    private fun setupAdapter() {
        binding.apply {
            promo.setHasFixedSize(true)
            promo.layoutManager = LinearLayoutManager(this@PromoActivity, LinearLayoutManager.VERTICAL, false)
            promo.adapter = adapter
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
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@PromoActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@PromoActivity, R.color.white))
                    snack.show()
                }
            }

    }
}