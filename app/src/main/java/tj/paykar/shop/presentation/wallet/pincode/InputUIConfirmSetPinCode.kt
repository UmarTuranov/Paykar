package tj.paykar.shop.presentation.wallet.pincode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.MainActivity
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivitySetPinCodeBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.PinCodeManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.enableErrorBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hideBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hidePinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showBorder
import tj.paykar.shop.domain.usecase.wallet.functions.showPinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.wallet.HomeActivity
import tj.paykar.shop.presentation.wallet.profile.ProfileActivity

class InputUIConfirmSetPinCode(private val mCode: String,
                               private val oldPinCode: String,
                               private val typedCode: String,
                               private val requestType: String,
                               private val binding: WalletActivitySetPinCodeBinding,
                               private val context: Context) {
    val progressDialog = CustomProgressDialog(context)

    fun inputUI() = with(binding) {
        when(mCode.length) {
            0 -> {
                showBorder(firstNumberLayout, context)
                hidePinCodeIcon(firstNumberTitle)
                hidePinCodeIcon(secondNumberTitle)
                hidePinCodeIcon(thirdNumberTitle)
                hidePinCodeIcon(fourthNumberTitle)
            }
            1 -> {
                hideBorder(firstNumberLayout, context)
                showBorder(secondNumberLayout, context)
                showPinCodeIcon(firstNumberTitle)
            }
            2 -> {
                hideBorder(secondNumberLayout, context)
                showBorder(thirdNumberLayout, context)
                showPinCodeIcon(secondNumberTitle)
            }
            3 -> {
                hideBorder(thirdNumberLayout, context)
                showBorder(fourthNumberLayout, context)
                showPinCodeIcon(thirdNumberTitle)
            }
            4 -> {
                showPinCodeIcon(fourthNumberTitle)
                if (mCode == typedCode) {
                    showBorder(firstNumberLayout, context)
                    showBorder(secondNumberLayout, context)
                    showBorder(thirdNumberLayout, context)
                    showBorder(fourthNumberLayout, context)
                    if (requestType == "set") {
                        setPinCodeRequest(mCode)
                    } else if (requestType == "reset") {
                        resetPinCodeRequest(oldPinCode, mCode)
                    }
                } else {
                    enableErrorBorder(firstNumberLayout, context)
                    enableErrorBorder(secondNumberLayout, context)
                    enableErrorBorder(thirdNumberLayout, context)
                    enableErrorBorder(fourthNumberLayout, context)
                    showViewWithAnimation(errorTitle)
                }
            }
        }
    }

    private fun setPinCodeRequest(code: String) {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = PinCodeManagerService().setPinCode(customerId, code, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            getUserInfo()
                        }
                        else {
                            progressDialog.dismiss()
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                setPinCodeRequest(code)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun resetPinCodeRequest(oldPinCode: String, newPinCode: String) {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val progressDialog = CustomProgressDialog(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = PinCodeManagerService().resetPinCode(customerId, oldPinCode, newPinCode, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            val activity = context as Activity
                            activity.finish()
                        }
                        else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                resetPinCodeRequest(oldPinCode, newPinCode)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun getUserInfo() {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val customerId = UserStorage(context).customerId
        val token = UserStorage(context).token
        Log.d("--RequestInfo", "token: $token")
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        }
        else {
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
                            UserStorage(context).saveUserInfo(userInfoResponse)
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            val activity = context as Activity
                            activity.finish()
                            Animatoo.animateZoom(context)
                            UserStorage(context).saveIsRegistration(false)
                        }  else {
                            requestResultCodeAlert(userInfoResponse?.ResultCode ?: 0, context, userInfoResponse?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте по позже!")
                            .setPositiveButton("На главную") {dialog, _ -> dialog.cancel()
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }
}