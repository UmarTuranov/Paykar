package tj.paykar.shop.presentation

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.snackbar.Snackbar
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityInternetConnectionBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.card.VirtualCardActivity

class InternetConnectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityInternetConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInternetConnectionBinding.inflate(layoutInflater)
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
        setupView()
    }

    private fun setupView() {
        binding.updatePage.setOnClickListener {
            val isOnline = MainManagerService().internetConnection(this)
            if (!isOnline) {
                val snack = Snackbar.make(binding.root, "Все еще нет подключения к интернету!", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                //snack.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                //snack.setAction("Посмотреть избранные") { }
                snack.show()
            } else {
                startActivity(Intent(this@InternetConnectionActivity, MainActivity::class.java))
            }
        }

        val card = UserStorageData(this).getInfoCard()

        if (card.token == "") {
            binding.virtualCard.isVisible = false
        } else {
            binding.virtualCard.isVisible = true
            binding.virtualCard.setOnClickListener {
                startActivity(Intent(this@InternetConnectionActivity, VirtualCardActivity::class.java))
            }
        }


    }
    
}