package tj.paykar.shop.presentation.wallet.pincode

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.SecurityStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityCodeBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.PinCodeManagerService
import tj.paykar.shop.domain.usecase.wallet.ResetFirebaseTokenManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.enableErrorBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hideBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hidePinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showBorder
import tj.paykar.shop.domain.usecase.wallet.functions.showPinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.profile.ProfileV2Activity
import tj.paykar.shop.presentation.wallet.HomeActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity
import tj.paykar.shop.presentation.wallet.qr.QrScannerActivity
import java.util.concurrent.Executor

class CodeActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityCodeBinding
    private lateinit var progressDialog: CustomProgressDialog
    private var code: String = ""
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var intentTo: String? = ""
    private var serviceId: Int? = 0
    private var paymentActivityType: String? = ""
    private var paymentSavedServiceAccount: String? = ""
    private var paymentSavedServiceAccount2: String? = ""
    private var paymentSavedServicePaymentSum: Double? = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityCodeBinding.inflate(layoutInflater)
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
        progressDialog = CustomProgressDialog(this@CodeActivity)
        window.navigationBarColor = this.resources.getColor(R.color.whiteToBlack)
        getIntentData()
        resetFtoken()
        setupView()
        setupBottomMenu()
    }

    private fun getIntentData() {
        val intent: Bundle? = intent.extras
        intentTo = intent?.getString("intentTo", "")
        serviceId = intent?.getInt("serviceId", 0)
        paymentActivityType = intent?.getString("paymentActivityType", "")
        paymentSavedServiceAccount = intent?.getString("paymentSavedServiceAccount", "")
        paymentSavedServiceAccount2 = intent?.getString("paymentSavedServiceAccount2", "")
        paymentSavedServicePaymentSum = intent?.getDouble("paymentSavedServicePaymentSum", 0.0)
        Log.d("--S Saved", paymentSavedServiceAccount ?: "")
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() = with(binding) {
        val fingerPrintSetting = SecurityStorage(this@CodeActivity).fingerPrintEnabled

        oneNumber.click()
        twoNumber.click()
        threeNumber.click()
        fourNumber.click()
        fiveNumber.click()
        sixNumber.click()
        sevenNumber.click()
        eightNumber.click()
        nineNumber.click()
        zeroNumber.click()

        backspaceLayout.setOnClickListener {
            addBlinkEffect(it)
            if (code != "") {
                Log.d("--P Password", code)
                backspaceUI(code)
                code = code.removeRange(code.length - 1 until code.length)
            }
        }

        forgetPinCodeTitle.setOnClickListener {
            addBlinkEffect(it)
            Log.d("--F Finger", fingerPrintSetting.toString())
            val intent = Intent(this@CodeActivity, ForgetPinCodeActivity::class.java)
            startActivity(intent)
        }

        fingerPrintLayout.setOnClickListener {
            addBlinkEffect(it)
            checkBiometricSupportAndAuthenticate()
        }

        if (fingerPrintSetting == true) {
            fingerPrintLayout.isVisible = true
            setupBiometricPrompt()
            checkBiometricSupportAndAuthenticate()
        } else if (fingerPrintSetting == false) {
            fingerPrintLayout.isInvisible = true
            loginTitle.text = "Введите PIN-код"
        }

        scroll.post {
            scroll.scrollTo(0, scrollRoot.height)
        }
    }

    private fun backspaceUI(mCode: String) = with(binding) {
        when (mCode.length) {
            0 -> {
                showBorder(firstNumberLayout, this@CodeActivity)
                hidePinCodeIcon(firstNumberIcon)
                hidePinCodeIcon(secondNumberIcon)
                hidePinCodeIcon(thirdNumberIcon)
                hidePinCodeIcon(fourthNumberIcon)
            }
            1 -> {
                hideBorder(secondNumberLayout, this@CodeActivity)
                hidePinCodeIcon(firstNumberIcon)
                showBorder(firstNumberLayout, this@CodeActivity)
            }
            2 -> {
                hideBorder(thirdNumberLayout, this@CodeActivity)
                hidePinCodeIcon(secondNumberIcon)
                showBorder(secondNumberLayout, this@CodeActivity)
            }
            3 -> {
                hideBorder(fourthNumberLayout, this@CodeActivity)
                hidePinCodeIcon(thirdNumberIcon)
                showBorder(thirdNumberLayout, this@CodeActivity)
            }
            4 -> {
                hidePinCodeIcon(fourthNumberIcon)
                HideViewWithAnimation().invisibleViewWithAnimation(errorTitle)
                hideBorder(firstNumberLayout, this@CodeActivity)
                hideBorder(secondNumberLayout, this@CodeActivity)
                hideBorder(thirdNumberLayout, this@CodeActivity)
            }
        }
    }

    private fun inputUI(mCode: String) = with(binding) {
        when (mCode.length) {
            0 -> {
                showBorder(firstNumberLayout, this@CodeActivity)
                hidePinCodeIcon(firstNumberIcon)
                hidePinCodeIcon(secondNumberIcon)
                hidePinCodeIcon(thirdNumberIcon)
                hidePinCodeIcon(fourthNumberIcon)
            }

            1 -> {
                hideBorder(firstNumberLayout, this@CodeActivity)
                showBorder(secondNumberLayout, this@CodeActivity)
                showPinCodeIcon(firstNumberIcon)
            }

            2 -> {
                hideBorder(secondNumberLayout, this@CodeActivity)
                showBorder(thirdNumberLayout, this@CodeActivity)
                showPinCodeIcon(secondNumberIcon)
            }

            3 -> {
                hideBorder(thirdNumberLayout, this@CodeActivity)
                showBorder(fourthNumberLayout, this@CodeActivity)
                showPinCodeIcon(thirdNumberIcon)
            }

            4 -> {
                showBorder(firstNumberLayout, this@CodeActivity)
                showBorder(secondNumberLayout, this@CodeActivity)
                showBorder(thirdNumberLayout, this@CodeActivity)
                showBorder(fourthNumberLayout, this@CodeActivity)
                showPinCodeIcon(fourthNumberIcon)
                Handler().postDelayed({
                    checkPinCode(code)
                }, 280)
            }

            else -> {}
        }
    }

    private fun checkPinCode(code: String) = with(binding) {
        val ipAddress = IpAddressStorage(this@CodeActivity).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this@CodeActivity)
        val customerId = UserStorage(this@CodeActivity).customerId ?: 0
        val token = UserStorage(this@CodeActivity).token
        val isOnline = MainManagerService().internetConnection(this@CodeActivity)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@CodeActivity)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val checkPinCode = PinCodeManagerService().checkPinCode(customerId, code, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response checkPinCode: ${checkPinCode.body()}")
                        val responseCheckPinCode = checkPinCode.body()
                        when (responseCheckPinCode?.ResultCode) {
                            0 -> {
                                getUserInfo(false)
                            }
                            25 -> {
                                progressDialog.dismiss()
                                showViewWithAnimation(errorTitle)
                                enableErrorBorder(firstNumberLayout, this@CodeActivity)
                                enableErrorBorder(secondNumberLayout, this@CodeActivity)
                                enableErrorBorder(thirdNumberLayout, this@CodeActivity)
                                enableErrorBorder(fourthNumberLayout, this@CodeActivity)
                            }
                            35 -> {
                                val intent = Intent(this@CodeActivity, SetPinCodeActivity::class.java)
                                intent.putExtra("requestType", "set")
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                progressDialog.dismiss()
                                requestResultCodeAlert(responseCheckPinCode?.ResultCode ?: 0, this@CodeActivity, responseCheckPinCode?.ResultDesc ?: "")
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@CodeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ввести PIN-код ещё раз!")
                            .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()
                                this@CodeActivity.code = ""
                                backspaceUI("")
                                hideBorder(secondNumberLayout, this@CodeActivity)
                                hideBorder(thirdNumberLayout, this@CodeActivity)
                                hideBorder(fourthNumberLayout, this@CodeActivity)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun MaterialButton.click() {
        this.setOnClickListener {
            if (code.length < 4) {
                code += this.text
                Log.d("--P Password", code)
                Log.d("--P Password", this.text.toString())
                inputUI(code)
                addBlinkEffect(it)
            }
        }
    }

    private fun resetFtoken() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId
        val token = UserStorage(this).token
        val ftoken = UserStorageData(this).getFirebaseToken()
        Log.d("--RequestInfo", "token: $token")
        val isOnline = MainManagerService().internetConnection(this)
        if (isOnline) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = ResetFirebaseTokenManagerService().resetFtoken(customerId ?: 0, ftoken, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                    }
                } catch (_: Exception) { }
            }
        }
    }

    private fun getUserInfo(showProgressDialog: Boolean) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId
        val token = UserStorage(this).token
        Log.d("--RequestInfo", "token: $token")
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            if (showProgressDialog) {
                progressDialog.show()
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val userInfo = UserManagerService().getUserInfo(customerId ?: 0, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        val userInfoResponse = userInfo.body()
                        Log.d("--RequestInfo", "response userInfo: $userInfoResponse")
                        if (userInfoResponse?.ResultCode == 0) {
                            UserStorage(this@CodeActivity).savePinCodeEntered(true)
                            UserStorage(this@CodeActivity).saveUserInfo(userInfoResponse)
                            HideViewWithAnimation().goneViewWithAnimation(binding.errorTitle)
                            when (intentTo) {
                                "PaymentActivity" -> {
                                    val intent = Intent(this@CodeActivity, PaymentActivity::class.java)
                                    intent.putExtra("serviceId", serviceId)
                                    intent.putExtra("activityType", paymentActivityType)
                                    intent.putExtra("savedServiceAccount", paymentSavedServiceAccount)
                                    intent.putExtra("savedServiceAccount2", paymentSavedServiceAccount2)
                                    intent.putExtra("savedServicePaymentSum", paymentSavedServicePaymentSum)
                                    startActivity(intent)
                                    Animatoo.animateZoom(this@CodeActivity)
                                }
                                "QrScannerActivity" -> {
                                    val intent = Intent(this@CodeActivity, QrScannerActivity::class.java)
                                    startActivity(intent)
                                    Animatoo.animateZoom(this@CodeActivity)
                                }
                                "SavedServices" -> {
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(this@CodeActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    Animatoo.animateZoom(this@CodeActivity)
                                }
                            }
                        }  else {
                            requestResultCodeAlert(userInfoResponse?.ResultCode ?: 0, this@CodeActivity, userInfoResponse?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@CodeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте по позже!")
                            .setPositiveButton("На главную") {dialog, _ -> dialog.cancel()
                                val intent = Intent(this@CodeActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun setupBiometricPrompt() {
        val executor: Executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    getUserInfo(true)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Вход по отпечатку")
            .setSubtitle("Используйте биометрические данные для входа")
            .setNegativeButtonText("Отмена")
            .build()
    }

    @SuppressLint("SetTextI18n")
    private fun checkBiometricSupportAndAuthenticate() = with(binding) {
        val biometricManager = BiometricManager.from(this@CodeActivity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> showBiometricPrompt()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                fingerPrintIcon.isInvisible = true
                loginTitle.text = "Введите PIN-код"
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                fingerPrintIcon.isInvisible = true
                loginTitle.text = "Введите PIN-код"
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                fingerPrintIcon.isInvisible = true
                loginTitle.text = "Введите PIN-код"
            }
            else -> {
                fingerPrintIcon.isInvisible = true
                loginTitle.text = "Введите PIN-код"
            }
        }
    }

    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
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

    override fun onResume() {
        super.onResume()
        setupBottomMenu()
    }
}