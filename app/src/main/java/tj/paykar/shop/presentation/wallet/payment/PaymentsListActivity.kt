package tj.paykar.shop.presentation.wallet.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import tj.paykar.shop.domain.adapter.wallet.PaymentListAdapter
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.PaymentCategory
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityPaymentsListBinding
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.PaymentManagerService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.data.TRANSFER_TO_BANK_CARDS_PAYMENT_ID
import tj.paykar.shop.data.TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.data.storage.wallet.IpAddressStorage

class PaymentsListActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityPaymentsListBinding
    private var accountNumber: String? = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityPaymentsListBinding.inflate(layoutInflater)
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
        getIntentData()
        getPaymentList()
        setupView()
    }

    private fun getIntentData() {
        val bundle: Bundle? = intent.extras
        accountNumber = bundle?.getString("selectPaymentAccount", "")
    }

    private fun setupView() = with(binding) {
        paykarWalletBtn.setOnClickListener {
            val intent = Intent(this@PaymentsListActivity, PaymentActivity::class.java)
            intent.putExtra("serviceId", TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID)
            intent.putExtra("serviceName", "На Paykar Wallet")
            intent.putExtra("selectPaymentAccount", accountNumber)
            startActivity(intent)
        }

        bankCardBtn.setOnClickListener {
            val intent = Intent(this@PaymentsListActivity, PaymentActivity::class.java)
            intent.putExtra("serviceId", TRANSFER_TO_BANK_CARDS_PAYMENT_ID)
            intent.putExtra("serviceName", "Перевод на карту")
            intent.putExtra("selectPaymentAccount", accountNumber)
            startActivity(intent)
        }

        walletBtn.setOnClickListener {
            val intent = Intent(this@PaymentsListActivity, SubPaymentsListActivity::class.java)
            intent.putExtra("categoryId", 3)
            intent.putExtra("categoryName", "Перевести деньги")
            intent.putExtra("hasCategory", false)
            intent.putExtra("selectPaymentAccount", accountNumber)
            startActivity(intent)
        }
    }

    private fun getPaymentList() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val progressDialog = CustomProgressDialog(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        var paymentList = ArrayList<PaymentCategory>()
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 2)
                    val request = PaymentManagerService().getPaymentCategory(customerId, 0, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        paymentList.addAll(response?.ServiceCategories ?: emptyList())
                        val adapter = PaymentListAdapter(paymentList, this@PaymentsListActivity, accountNumber ?: "")
                        binding.paymentsListRecycler.adapter = adapter
                        binding.paymentsListRecycler.setHasFixedSize(true)
                        AnimateViewHeight().showView(binding.paymentsListRecycler, binding.paymentsListRecycler, 500)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@PaymentsListActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробуйте снова") {_, _ ->
                                getPaymentList()
                            }
                            .show()
                    }
                }
            }
        }
    }
}
