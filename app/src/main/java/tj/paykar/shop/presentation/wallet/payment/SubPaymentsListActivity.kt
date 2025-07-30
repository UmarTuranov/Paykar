package tj.paykar.shop.presentation.wallet.payment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivitySubPaymentsListBinding
import tj.paykar.shop.domain.adapter.wallet.PaymentListAdapter
import tj.paykar.shop.domain.adapter.wallet.ServiceListAdapter
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.PaymentManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation

@Suppress("DEPRECATION")
class SubPaymentsListActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivitySubPaymentsListBinding
    private var accountNumber: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivitySubPaymentsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }

        setupView()
        val bundle: Bundle? = intent.extras
        accountNumber = bundle?.getString("selectPaymentAccount")
        Log.d("--A AccountNumberSubPL", accountNumber.toString())
    }

    private fun setupView() {
        val bundle: Bundle? = intent.extras
        val categoryId: Int? = bundle?.getInt("categoryId", 0)
        val categoryName: String? = bundle?.getString("categoryName")
        val hasCategory: Boolean? = bundle?.getBoolean("hasCategory")
        binding.materialToolbar.title = categoryName
        Log.d("--C", "id: $categoryId, categoryName: $categoryName")
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            if (hasCategory == true) {
                getPaymentSubCategory()
            } else {
                getPaymentService()
            }
        }

        binding.backBtnLoading.setOnClickListener {
            addBlinkEffect(it)
            onBackPressed()
        }
    }

    private fun getPaymentSubCategory() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val bundle: Bundle? = intent.extras
        val categoryId: Int? = bundle?.getInt("categoryId", 0)
        showViewWithAnimation(binding.loadingCard, 300)
        Handler().postDelayed({
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 2)
                    val request = PaymentManagerService().getPaymentCategory(customerId, categoryId ?: 0, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        val adapter = PaymentListAdapter(response?.ServiceCategories ?: emptyList(), this@SubPaymentsListActivity, accountNumber ?: "")
                        binding.paymentsListRecycler.adapter = adapter
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                        MaterialAlertDialogBuilder(this@SubPaymentsListActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {_, _ ->
                                getPaymentSubCategory()
                            }
                            .show()
                    }
                }
            }
        }, 500)
    }

    private fun getPaymentService() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val bundle: Bundle? = intent.extras
        val categoryId: Int? = bundle?.getInt("categoryId", 0)
        showViewWithAnimation(binding.loadingCard, 300)
        Handler().postDelayed({
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 2)
                    val request = PaymentManagerService().getPaymentService(customerId, categoryId ?: 0, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        val adapter = ServiceListAdapter(response?.Services ?: emptyList(), this@SubPaymentsListActivity, accountNumber ?: "")
                        binding.paymentsListRecycler.adapter = adapter
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@SubPaymentsListActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {_, _ ->
                                getPaymentService()
                            }
                            .show()
                    }
                }
            }
        }, 500)
    }
}