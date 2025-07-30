package tj.paykar.shop.presentation.shop.basket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.AddressModel
import tj.paykar.shop.data.model.shop.BasketModel
import tj.paykar.shop.data.model.shop.ProductItems
import tj.paykar.shop.data.model.shop.RecommendationProductModel
import tj.paykar.shop.data.storage.AddressStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityBasketBinding
import tj.paykar.shop.databinding.BottomSheetAddressBinding
import tj.paykar.shop.databinding.BottomSheetAddressInfoBinding
import tj.paykar.shop.domain.adapter.address.AddressAdapter
import tj.paykar.shop.domain.adapter.basket.BasketAdapter
import tj.paykar.shop.domain.adapter.basket.RecommendProductAdapter
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.shop.RecommendedProductManagerService
import tj.paykar.shop.domain.usecase.shop.VacationDayManagerService
import tj.paykar.shop.presentation.InternetConnectionActivity

class BasketActivity : AppCompatActivity(),
    tj.paykar.shop.domain.reprository.shop.RecommendProductClickManager,
    tj.paykar.shop.domain.reprository.shop.AddressSelectedManager {

    lateinit var binding: ActivityBasketBinding
    private var basketResposne: BasketModel = BasketModel("0", "0", "0", ArrayList<ProductItems>())
    private val adapter = BasketAdapter(this@BasketActivity) { totalPrice, count ->
        refreshActivity(totalPrice, count)
    }
    lateinit var bottomSheetRecommend: BottomSheetDialog
    lateinit var bottomSheetRecommendView: View
    var recommendProductList = ArrayList<RecommendationProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBasketBinding.inflate(layoutInflater)
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
        binding.resume.isVisible = false
        binding.emptyBasket.isVisible = true
        binding.emptyBasketTitle.isVisible = true

        bottomSheetRecommend = BottomSheetDialog(
            this, R.style.BottomSheetDialogTheme
        )
        bottomSheetRecommendView = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_recommend_products,
            findViewById(R.id.recommendBSh)
        )
        bottomSheetRecommend.setContentView(bottomSheetRecommendView)
        bottomSheetRecommend.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

        checkInternet()

    }

    private fun checkInternet() {
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            startActivity(Intent(this, InternetConnectionActivity::class.java))
        } else {
            val operatingModeUrl = UserStorageData(this).getOperatingModeUrl()
            Glide.with(this)
                .load(operatingModeUrl)
                .placeholder(R.drawable.nophoto)
                .into(binding.workHours)

            checkVacationDay()
            getRecommendProducts()
            setupBottomSheet()
            setupAdapter()
            service()
        }
    }

    private fun checkVacationDay(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val restDay = VacationDayManagerService().restDay().body()
                withContext(Dispatchers.Main){
                    if (restDay?.response == true){
                        MaterialAlertDialogBuilder(this@BasketActivity)
                            .setTitle(restDay.title)
                            .setMessage(restDay.description)
                            .show()

                        binding.resume.isEnabled = false
                    }
                }
            }catch (_:Exception){}
        }
    }

    private fun getRecommendProducts(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = UserStorageData(this@BasketActivity).getUserId()
                recommendProductList = RecommendedProductManagerService().productList(userId)
                withContext(Dispatchers.Main){
                    val recommendRV: RecyclerView = bottomSheetRecommendView.findViewById(R.id.recommendRV)
                    val recAdapter = RecommendProductAdapter(this@BasketActivity, this@BasketActivity)
                    recAdapter.productList = recommendProductList
                    recommendRV.setHasFixedSize(true)
                    recommendRV.layoutManager = LinearLayoutManager(this@BasketActivity, LinearLayoutManager.HORIZONTAL, false)
                    recommendRV.adapter = recAdapter
                }
            }catch (e:Exception){
                Log.d("--E Error", e.toString())
            }
        }
    }

    private fun setupBottomSheet(){
        val continueBtn: MaterialButton = bottomSheetRecommendView.findViewById(R.id.continueBtn)
        continueBtn.setOnClickListener {
            val basketTotalPrice = basketResposne.totalPrice?.toDouble()
            val addressList = AddressStorage(this@BasketActivity).getAddressList() ?: arrayListOf()
            if (addressList.isNotEmpty()){
                val intent = Intent(this@BasketActivity, AddressConfirmActivityV2::class.java)
                if (adapter.totalPrice != 0.0){
                    intent.putExtra("orderPrice", adapter.totalPrice)
                }else{
                    intent.putExtra("orderPrice", basketTotalPrice)
                }
                intent.putExtra("discountPrice", "0")
                intent.putExtra("deliveryPrice", basketResposne.deliveryPrice)
                startActivity(intent)
            }else{
                showAddressBottomSheet()
            }
        }
    }

    private fun showAddressBottomSheet() {
        val addressList = ArrayList<AddressModel>()
        addressList.addAll(AddressStorage(this).getAddressList() ?: arrayListOf())
        val adapter = AddressAdapter(this@BasketActivity, addressList, "createNewAddress", this)
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetAddressBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            addNewAddress.setOnClickListener {
                showAddressInfoBottomSheet(adapter)
            }
            addressRecycler.setHasFixedSize(true)
            addressRecycler.layoutManager = LinearLayoutManager(this@BasketActivity, LinearLayoutManager.VERTICAL, false)
            addressRecycler.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAddressInfoBottomSheet(adapter: AddressAdapter) {
        val addressList = ArrayList<AddressModel>()
        addressList.addAll(AddressStorage(this).getAddressList() ?: arrayListOf())
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetAddressInfoBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            saveBtn.setOnClickListener {
                if (streetText.text.isNullOrEmpty()) {
                    street.error = "Обязательное поле"
                } else {
                    val myAddress = AddressModel(streetText.text.toString(), houseText.text.toString(), entranceText.text.toString(), flatText.text.toString(), floorText.text.toString(), true)
                    AddressStorage(this@BasketActivity).saveAddress(myAddress)
                    bottomSheetDialog.dismiss()
                    adapter.updateList()
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun setupAdapter() {
        binding.apply {
            basket.setHasFixedSize(true)
            basket.layoutManager =
                LinearLayoutManager(this@BasketActivity, LinearLayoutManager.VERTICAL, false)
            basket.adapter = adapter
            cleanButton.setOnClickListener {
                basketTitle.text = "Корзина"
                UserStorageData(this@BasketActivity).saveBasketCount(0)
                binding.cleanButton.isVisible = false
                adapter.deleteAll()
            }

            resume.setOnClickListener {

                if (recommendProductList.isEmpty()){
                    val basketTotalPrice = basketResposne.totalPrice?.toDouble()
                    val addressList = AddressStorage(this@BasketActivity).getAddressList() ?: arrayListOf()
                    if (addressList.isNotEmpty()){
                        val intent = Intent(this@BasketActivity, AddressConfirmActivityV2::class.java)
                        if (adapter.totalPrice != 0.0){
                            intent.putExtra("orderPrice", adapter.totalPrice)
                        }else{
                            intent.putExtra("orderPrice", basketTotalPrice)
                        }
                        intent.putExtra("discountPrice", "0")
                        intent.putExtra("deliveryPrice", basketResposne.deliveryPrice)
                        startActivity(intent)
                    }else{
                        showAddressBottomSheet()
                    }
                }
                else{
                    bottomSheetRecommend.show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun service() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                adapter.sendRequest()
                withContext(Dispatchers.Main) {
                    basketResposne = adapter.basketResposne
                    adapter.notifyDataSetChanged()
                    binding.apply {
                        if (basketResposne.totalPrice != "0") {
                            val basketTotalPrice = String.format("%.2f", basketResposne.totalPrice?.toDouble())
                            basketTitle.text = "$basketTotalPrice сомони"
                            UserStorageData(this@BasketActivity).saveBasketCount(basketResposne.productList.size)
                            binding.cleanButton.isVisible = true
                            binding.resume.isVisible = true
                            binding.emptyBasket.isVisible = false
                            binding.emptyBasketTitle.isVisible = false
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@BasketActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@BasketActivity, R.color.white))
                    snack.show()

                    binding.basketTitle.text = "Корзина"
                    UserStorageData(this@BasketActivity).saveBasketCount(0)
                    binding.cleanButton.isVisible = false
                }
                Log.e("Error in Basket", e.message.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshActivity(title: String, count: Int) {
        if (count != 0){
            binding.basketTitle.text = "$title сомони"
            UserStorageData(this@BasketActivity).saveBasketCount(count)
        } else {
            binding.basketTitle.text = "Корзина"
            binding.cleanButton.isVisible = false
            UserStorageData(this@BasketActivity).saveBasketCount(0)
        }
    }

    override fun updateBasket() {
        service()
    }

    override fun onSelected(address: AddressModel) {}

}