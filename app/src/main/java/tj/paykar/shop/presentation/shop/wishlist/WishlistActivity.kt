package tj.paykar.shop.presentation.shop.wishlist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityWishlistBinding
import tj.paykar.shop.domain.adapter.wishlist.WishlistAdapter

class WishlistActivity : AppCompatActivity() {

    lateinit var binding: ActivityWishlistBinding
    private val adapter = WishlistAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWishlistBinding.inflate(layoutInflater)
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
        binding.cleanButton.isVisible = false
        setupAdapter()
        service()

    }

    private fun setupAdapter() {
        binding.apply {
            wishlist.setHasFixedSize(true)
            wishlist.layoutManager = LinearLayoutManager(this@WishlistActivity, LinearLayoutManager.VERTICAL, false)
            wishlist.adapter = adapter
            cleanButton.setOnClickListener { adapter.deleteAll() }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                adapter.sendRequest()
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                    binding.apply {
                        if (adapter.whishListProduct.size != 0) {
                            binding.cleanButton.isVisible = true
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e("Error", e.message.toString())
                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this@WishlistActivity, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this@WishlistActivity, R.color.white))
                snack.show()
            }

        }
    }
}