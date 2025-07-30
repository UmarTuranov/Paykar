package tj.paykar.shop.presentation.wallet.pincode

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityForgetPinCodeBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.PinCodeManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.CheckDocumentManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard

class ForgetPinCodeActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityForgetPinCodeBinding
    private var activityType: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityForgetPinCodeBinding.inflate(layoutInflater)
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
        activityType = "confirm"
        Log.d("--Steps", "forgetPinCodeRequest")
        forgetPinCodeRequest()
        setupView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@ForgetPinCodeActivity, currentFocus ?: View(this@ForgetPinCodeActivity))
            false
        }

        confirmButton.setOnClickListener {
            if (activityType == "confirm") {
                if (confirmCodeEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@ForgetPinCodeActivity)
                        .setTitle("Код подтверждения")
                        .setMessage("Введите код подтверждения для сброса PIN-кода")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                } else {
                    Log.d("--Steps", "confirmForgetPinCodeRequest")
                    confirmForgetPinCodeRequest(confirmCodeEditText.text.toString())
                }
            } else {
                if (taxIdEditText.text.isNullOrEmpty()) {
                    taxIdLayout.isErrorEnabled = true
                    taxIdLayout.error = "Обязательное поле"
                } else {
                    Log.d("--Steps", "CheckDocument")
                    checkDocument(taxIdEditText.text.toString())
                }
            }
        }
    }

    private fun forgetPinCodeRequest() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val progressDialog = CustomProgressDialog(this@ForgetPinCodeActivity)
        val customerId = UserStorage(this@ForgetPinCodeActivity).customerId ?: 0
        val token = UserStorage(this@ForgetPinCodeActivity).token
        val isOnline = MainManagerService().internetConnection(this@ForgetPinCodeActivity)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@ForgetPinCodeActivity)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = PinCodeManagerService().forgetPinCode(customerId, deviceInfo, requestInfo)
                    val userInfo = UserManagerService().getUserInfo(customerId, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        Log.d("--RequestInfo", "userInfoResponse: ${userInfo.body()}")
                        val response = request.body()
                        val userInfoResponse = userInfo.body()
                        if (response?.ResultCode == 0 && userInfoResponse?.ResultCode == 0) {
                            binding.confirmButton.isEnabled = true
                            UserStorage(this@ForgetPinCodeActivity).saveUserInfo(userInfoResponse)
                        } else {
                            requestResultCodeAlert(userInfoResponse?.ResultCode ?: 0, this@ForgetPinCodeActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@ForgetPinCodeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте попытку по позже!")
                            .setPositiveButton("OK") {dialog, _ -> dialog.cancel()}
                            .show()
                    }
                }
            }
        }
    }

    private fun confirmForgetPinCodeRequest(confirmCode: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val progressDialog = CustomProgressDialog(this@ForgetPinCodeActivity)
        val customerId = UserStorage(this@ForgetPinCodeActivity).customerId ?: 0
        val token = UserStorage(this@ForgetPinCodeActivity).token
        val isOnline = MainManagerService().internetConnection(this@ForgetPinCodeActivity)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@ForgetPinCodeActivity)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = PinCodeManagerService().confirmForgetPinCode(customerId, confirmCode, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            val identificationStatus = UserStorage(this@ForgetPinCodeActivity).getUserInfo()?.IdentificationRequest?.RequestState
                            if (identificationStatus == 1) {
                                AnimateViewHeight().showView(binding.taxIdLayout, binding.taxIdLayout)
                                binding.confirmButton.text = "Сбросить"
                                activityType = "checkDocument"
                                binding.confirmCodeLayout.isEnabled = false
                                binding.confirmCodeEditText.isEnabled = false
                            } else {
                                val intent = Intent(this@ForgetPinCodeActivity, SetPinCodeActivity::class.java)
                                intent.putExtra("requestType", "set")
                                startActivity(intent)
                                finish()
                            }
                        }  else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@ForgetPinCodeActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@ForgetPinCodeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                confirmForgetPinCodeRequest(confirmCode)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun checkDocument(taxId: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val progressDialog = CustomProgressDialog(this@ForgetPinCodeActivity)
        val customerId = UserStorage(this@ForgetPinCodeActivity).customerId ?: 0
        val token = UserStorage(this@ForgetPinCodeActivity).token
        val isOnline = MainManagerService().internetConnection(this@ForgetPinCodeActivity)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@ForgetPinCodeActivity)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = CheckDocumentManagerService().checkDocument(customerId, taxId, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            val intent = Intent(this@ForgetPinCodeActivity, SetPinCodeActivity::class.java)
                            intent.putExtra("requestType", "set")
                            startActivity(intent)
                            finish()
                        }  else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@ForgetPinCodeActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@ForgetPinCodeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                checkDocument(taxId)
                            }
                            .show()
                    }
                }
            }
        }
    }
}