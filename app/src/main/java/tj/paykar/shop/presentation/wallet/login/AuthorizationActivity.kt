package tj.paykar.shop.presentation.wallet.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.RequestDeviceTypeInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityAuthorizationBinding
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.profile.ProfileV2Activity
import tj.paykar.shop.presentation.wallet.pincode.CodeActivity

class AuthorizationActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityAuthorizationBinding
    private lateinit var progressDialog: CustomProgressDialog
    private var requestType: String = "auth"
    private var customerId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletActivityAuthorizationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
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
        window.navigationBarColor = this.resources.getColor(R.color.whiteToBlack)
        progressDialog = CustomProgressDialog(this)
        setupView()
        setupBottomMenu()
    }

    private fun removeCountryCode(phoneNumber: String): String {
        return if (phoneNumber.startsWith("+992")) {
            phoneNumber.replaceFirst("+992", "")
        } else if (phoneNumber.startsWith("992")) {
            phoneNumber.replaceFirst("992", "")
        } else {
            phoneNumber
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        registrationBtn.setOnClickListener {
            val intent = Intent(this@AuthorizationActivity, RegistrationActivity::class.java)
            startActivity(intent)
            Animatoo.animateZoom(this@AuthorizationActivity)
        }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@AuthorizationActivity, currentFocus ?: View(this@AuthorizationActivity))
            false
        }

        val userShopData = UserStorageData(this@AuthorizationActivity).getUser()
        val cardUserData = UserStorageData(this@AuthorizationActivity).getInfoCard()
        if (userShopData.id != 0 && cardUserData.clientId == 0) {
            val phoneEditable = MainManagerService().toEditable(removeCountryCode(userShopData.phone ?: ""))
            phoneEditText.text = phoneEditable
        } else if (cardUserData.clientId != 0) {
            val phoneEditable = MainManagerService().toEditable(removeCountryCode(cardUserData.phone ?: ""))
            phoneEditText.text = phoneEditable
            Log.d("--D Phone", cardUserData.phone ?: "")
        }

        phoneEditText.doOnTextChanged { _, _, _, _ ->
            requestType = "auth"
            authorizationBtn.text = "Далее"
            AnimateViewHeight().hideView(confirmCodeLayout)
            confirmCodeEditText.text?.clear()
        }

        authorizationBtn.setOnClickListener {
            if (requestType == "auth") {
                if (phoneEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@AuthorizationActivity)
                        .setTitle("Номер телефона")
                        .setMessage("Введите номер телефона для авторизации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                } else {
                    authorizationRequest(phoneEditText.text.toString())
                }
            } else if (requestType == "confirmation") {
                if (confirmCodeEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@AuthorizationActivity)
                        .setTitle("Код подтверждения")
                        .setMessage("Введите код подтверждения для авторизации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                } else {
                    confirmAuthorizationRequest(confirmCodeEditText.text.toString())
                }
            }
        }
    }

    private fun authorizationRequest(phoneNumber: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val deviceModel = DeviceInfo().getDeviceModel()
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = UserManagerService().login(phoneNumber, RequestDeviceTypeInfoModel(2, imei ?: "", BuildConfig.VERSION_NAME, deviceModel), RequestInfoModel("TJK", ipAddress, 3))
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        when (response?.ResultCode) {
                            0 -> {
                                customerId = response.CustomerId ?: 0
                                AnimateViewHeight().showView(binding.confirmCodeLayout, binding.confirmCodeLayout)
                                binding.authorizationBtn.text = "Авторизоваться"
                                showViewWithAnimation(binding.authorizationBtn)
                                requestType = "confirmation"
                            }
                            3 -> {
                                MaterialAlertDialogBuilder(this@AuthorizationActivity)
                                    .setTitle("Пользователь не найден!")
                                    .setMessage("Пользователь не зарегистрирован, зарегистрируйтесь пожалуйста!")
                                    .setPositiveButton("OK") {_, _ ->
                                        val intent = Intent(this@AuthorizationActivity, RegistrationActivity::class.java)
                                        intent.putExtra("phoneNumber", binding.phoneEditText.text.toString())
                                        intent.putExtra("notFound", true)
                                        startActivity(intent)
                                    }
                                    .show()
                            }
                            else -> {
                                requestResultCodeAlert(response?.ResultCode ?: 0, this@AuthorizationActivity, response?.ResultDesc ?: "")
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@AuthorizationActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте по позже!")
                            .setCancelable(false)
                            .setPositiveButton("Понятно") {_, _ ->
                                val intent = Intent(this@AuthorizationActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun getPaykarUserInfo() = with(binding) {
        val isOnline = MainManagerService().internetConnection(this@AuthorizationActivity)
        val phoneNumber = phoneEditText.text.toString()
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@AuthorizationActivity)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = UserManagerService().walletUserInfoPaykar(phoneNumber)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        val response = request.body()
                        if (response != null) {
                            UserStorage(this@AuthorizationActivity).savePaykarUserInfo(response)
                        }
                        val intent = Intent(this@AuthorizationActivity, CodeActivity::class.java)
                        startActivity(intent)
                        finish()
                        Animatoo.animateCard(this@AuthorizationActivity)
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@AuthorizationActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Что то пошло не так повторите попытку по позже!")
                            .setCancelable(false)
                            .setPositiveButton("Понятно") {_, _ ->
                                val intent = Intent(this@AuthorizationActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun confirmAuthorizationRequest(confirmCode: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val deviceModel = DeviceInfo().getDeviceModel()
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceTypeInfoModel(2, imei ?: "", BuildConfig.VERSION_NAME, deviceModel)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = UserManagerService().confirmLoginUser(customerId, confirmCode, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            UserStorage(this@AuthorizationActivity).saveUser(customerId, binding.phoneEditText.text.toString(), response.DeviceInfo.Token ?: "")
                            getPaykarUserInfo()
                        } else {
                            progressDialog.dismiss()
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@AuthorizationActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@AuthorizationActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {_, _ ->
                                confirmAuthorizationRequest(confirmCode)
                            }
                            .show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupBottomMenu()
    }

    private fun setupBottomMenu() {
        val ptoken = PaykarIdStorage(this).userToken
        if (ptoken == "") {
            binding.bottomMenu.setMenuResource(R.menu.bottom_menu_not_authorized)
        } else {
            binding.bottomMenu.setMenuResource(R.menu.button_menu)
        }
        binding.bottomMenu.setItemSelected(R.id.wallet, true)
        binding.bottomMenu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)}
                R.id.catalog -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    startActivity(intent)
                }
                R.id.wallet -> {}
                R.id.card -> {
                    val intent = Intent(this, VirtualCardActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}