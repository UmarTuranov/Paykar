package tj.paykar.shop.presentation.shop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.PreferencesModel
import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.ActivityShopBinding
import tj.paykar.shop.domain.adapter.catalog.CatalogAdapter
import tj.paykar.shop.domain.adapter.home.ProfitablyAdapter
import tj.paykar.shop.domain.adapter.home.PromoHomeAdapter
import tj.paykar.shop.domain.adapter.home.ReviewAdapter
import tj.paykar.shop.domain.adapter.preferences.PreferencesAdapter
import tj.paykar.shop.domain.adapter.recommendation.RecommendationAdapter
import tj.paykar.shop.domain.usecase.HomePageManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.shop.PreferencesManagerService
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.presentation.InternetConnectionActivity
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.profile.ProfileV2Activity
import tj.paykar.shop.presentation.shop.basket.BasketActivity
import tj.paykar.shop.presentation.shop.search.SearchActivity
import tj.paykar.shop.presentation.wallet.WalletActivity
import tj.paykar.shop.presentation.webview.WebChatActivity

class ShopActivity : AppCompatActivity(),
    tj.paykar.shop.domain.reprository.shop.ProductSelectManager,
    tj.paykar.shop.domain.reprository.shop.PreferencesItemClickManager {

    lateinit var binding: ActivityShopBinding
    private var userShop: UserStorageModel = UserStorageModel(0, "", "", "")
    private var savePreferencesButton: MaterialButton? = null
    private var isOnline: Boolean = false
    private var catalogAdapter = CatalogAdapter(arrayListOf())
    private val recommendationAdapter = RecommendationAdapter(this)
    private val profitablyAdapter = ProfitablyAdapter(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShopBinding.inflate(layoutInflater)
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
        val user = UserStorageData(this).getUser()
        userShop = user
        isOnline = MainManagerService().internetConnection(this)
        setupView()
        getShopData()
        checkUserPreferences()
    }

    private fun setupView() = with(binding) {
        window.navigationBarColor = this@ShopActivity.resources.getColor(R.color.whiteToBlack)

        searchButton.setOnClickListener {
            val intent = Intent(this@ShopActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        chatButton.setOnClickListener {
            val intent = Intent(this@ShopActivity, WebChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getShopData() {
        if (!isOnline) {
            val intent = Intent(this, InternetConnectionActivity::class.java)
            startActivity(intent)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = HomePageManagerService().getShopData(userShop.id)
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.loadingView)
                        if (request.isSuccessful) {
                            val response = request.body()
                            binding.apply {

                                recommendLinear.isVisible = !response?.recomendationList.isNullOrEmpty()
                                recommendationAdapter.productList = response?.recomendationList ?: arrayListOf()
                                recommendRecycler.adapter = recommendationAdapter

                                saleLinear.isVisible = !response?.profitablyList.isNullOrEmpty()
                                profitablyAdapter.productList = response?.profitablyList ?: arrayListOf()
                                saleRecycler.adapter = profitablyAdapter

                                catalogAdapter = CatalogAdapter(response?.catalogList ?: arrayListOf())
                                catalogRecycler.adapter = catalogAdapter
                            }
                        } else {
                            val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(this@ShopActivity, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(this@ShopActivity, R.color.white))
                            snack.show()
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getBasketData()
        setupButtonMenu()
    }

    @SuppressLint("SetTextI18n")
    private fun getBasketData() {
        if (!isOnline) {
            startActivity(Intent(this, InternetConnectionActivity::class.java))
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val basketData = getBasketService().basketItems(userShop.id)
                    withContext(Dispatchers.Main) {
                        UserStorageData(this@ShopActivity).saveBasketCount(basketData.productList.size)
                        binding.basketButton.text = "${basketData.totalPrice} с"
                        Log.d("--R RequestInfo", basketData.toString())
                        if (basketData.productList.isNotEmpty()) {
                            binding.basketButton.show()
                        } else {
                            binding.basketButton.hide()
                        }

                        binding.basketButton.setOnClickListener {
                            val intent = Intent(this@ShopActivity, BasketActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(this@ShopActivity, R.color.statusBarBackground))
                        snack.setTextColor(ContextCompat.getColor(this@ShopActivity, R.color.white))
                        snack.show()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkUserPreferences() {
        val userId = UserStorageData(this).getUserId()
        if (userId != 0) {
            if (!UserStorageData(this).selectPrefOrNo()) {

                val bottomSheet = BottomSheetDialog(
                    this, R.style.BottomSheetDialogTheme
                )
                val bottomSheetView = LayoutInflater.from(this).inflate(
                    R.layout.bottom_sheet_preferences,
                    findViewById(R.id.preferencesBSh)
                )
                bottomSheet.setContentView(bottomSheetView)
                bottomSheet.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

                bottomSheet.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val dialog =
                        bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    val layoutParams = dialog!!.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    dialog.layoutParams = layoutParams
                    val behavior = BottomSheetBehavior.from(dialog)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

                val preferencesAdapter = PreferencesAdapter(this, this)
                savePreferencesButton = bottomSheetView.findViewById(R.id.saveBtn)
                val preferencesRV: RecyclerView = bottomSheetView.findViewById(R.id.preferencesRV)
                val loadingAnimation: CardView = bottomSheetView.findViewById(R.id.loading)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val preferencesList = PreferencesManagerService().getPreferencesList()
                        withContext(Dispatchers.Main) {
                            preferencesRV.setHasFixedSize(true)
                            val layoutManager = FlexboxLayoutManager(this@ShopActivity)
                            layoutManager.flexDirection = FlexDirection.ROW
                            layoutManager.justifyContent = JustifyContent.FLEX_START
                            preferencesRV.layoutManager = layoutManager
                            preferencesAdapter.preferencesList = preferencesList
                            preferencesRV.adapter = preferencesAdapter
                            bottomSheet.show()
                        }
                    } catch (e: Exception) {
                        Log.d("--E Error", e.toString())
                        if (!isFinishing) {
                            withContext(Dispatchers.Main) {
                                MaterialAlertDialogBuilder(this@ShopActivity)
                                    .setTitle("Сервер недоступен!")
                                    .setMessage("Попробуйте позже")
                                    .show()
                            }
                        }
                    }
                }

                savePreferencesButton?.setOnClickListener {
                    val isOnline = MainManagerService().internetConnection(this@ShopActivity)
                    if (isOnline) {
                        loadingAnimation.isVisible = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                PreferencesManagerService().savePreferences(
                                    userId,
                                    preferencesAdapter.preferencesList
                                )
                                withContext(Dispatchers.Main) {
                                    UserStorageData(this@ShopActivity).saveSelectedPref()
                                    bottomSheet.dismiss()
                                }
                            } catch (e: Exception) {
                                Log.d("--E Error", e.toString())
                                withContext(Dispatchers.Main) {
                                    MaterialAlertDialogBuilder(this@ShopActivity)
                                        .setTitle("Сервер недоступен!")
                                        .setMessage("Попробуйте позже")
                                        .show()
                                }
                            }
                        }
                    } else {
                        MaterialAlertDialogBuilder(this@ShopActivity)
                            .setTitle("Нет интернета!")
                            .setMessage("Включите интернет и повторите попытку")
                            .show()
                    }
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
                    binding.bottomMenu.showBadge(R.id.home)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.catalog -> {
                }
                R.id.wallet -> {
                    binding.bottomMenu.showBadge(R.id.wallet)
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

    override fun checkProduct(isSelected: Boolean) {
        getBasketData()
    }

    override fun checkSelected(preferences: ArrayList<PreferencesModel>) {
        savePreferencesButton?.isEnabled = false
        for (p in preferences) {
            if (p.selected == true) {
                savePreferencesButton?.isEnabled = true
                break
            }
        }
    }
}