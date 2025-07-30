package tj.paykar.shop.presentation.shop.catalog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.ActivityCatalogSectionProductBinding
import tj.paykar.shop.domain.adapter.catalog.CatalogSectionProductAdapter
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.profile.ProfileV2Activity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.wallet.WalletActivity

class CatalogSectionProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityCatalogSectionProductBinding
    lateinit var shimmer: ShimmerFrameLayout
    private var position: Int = 0
    private var sectionNameIn: String = ""
    lateinit var productAdapter: CatalogSectionProductAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCatalogSectionProductBinding.inflate(layoutInflater)
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
        getPutIntent()
        window.navigationBarColor = this.resources.getColor(R.color.whiteToBlack)

        productAdapter = CatalogSectionProductAdapter(this, position)
        binding.apply {
            shimmer = shimmerLayout
            shimmer.startShimmer()
            sectionName.text = sectionNameIn
            productList.setHasFixedSize(true)
            productList.layoutManager = LinearLayoutManager(this@CatalogSectionProductActivity, LinearLayoutManager.VERTICAL, false)
            productList.adapter = productAdapter

            backBtn.setOnClickListener {
                addBlinkEffect(it)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupButtonMenu()

        setupAdapter()
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        position = bundle?.getInt("sectionId") ?: 0
        sectionNameIn = bundle?.getString("sectionName") ?: "Название категории"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {
        binding.apply {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    productAdapter.sendRequest()
                    withContext(Dispatchers.Main) {
                        shimmer.stopShimmer()
                        shimmer.visibility = View.GONE
                        productList.visibility = View.VISIBLE
                        productAdapter.notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@CatalogSectionProductActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@CatalogSectionProductActivity, R.color.white))
                    snack.show()
                }

            }
        }
    }

    private fun setupButtonMenu() {
        val ptoken = PaykarIdStorage(this).userToken
        if (ptoken == "") {
            binding.bottomMenu.setMenuResource(R.menu.bottom_menu_not_authorized)
        } else {
            binding.bottomMenu.setMenuResource(R.menu.button_menu)
        }
        binding.bottomMenu.setItemSelected(R.id.catalog, true)
        binding.bottomMenu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.catalog -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    startActivity(intent)
                }
                R.id.wallet -> {
                    val intent = Intent(this, WalletActivity::class.java)
                    startActivity(intent)
                }
                R.id.card -> {
                    val intent = Intent(this, VirtualCardActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }

}