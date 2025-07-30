package tj.paykar.shop.presentation.wallet.pincode

import android.content.Context
import android.transition.TransitionManager
import android.util.Log
import androidx.core.view.isGone
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivitySetPinCodeBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.PinCodeManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.animateTextChange
import tj.paykar.shop.domain.usecase.wallet.functions.backspaceUI
import tj.paykar.shop.domain.usecase.wallet.functions.enableErrorBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hideBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hidePinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showBorder
import tj.paykar.shop.domain.usecase.wallet.functions.showPinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.data.storage.wallet.IpAddressStorage

class InputUIOldPinCode(private val mCode: String,
                        private val binding: WalletActivitySetPinCodeBinding,
                        private val context: Context,
                        private val activity: SetPinCodeActivity
) {

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
                showBorder(firstNumberLayout, context)
                showBorder(secondNumberLayout, context)
                showBorder(thirdNumberLayout, context)
                showBorder(fourthNumberLayout, context)
                showPinCodeIcon(fourthNumberTitle)

                checkOldPinCode(mCode)
            }

            else -> {}
        }
    }

    private fun checkOldPinCode(code: String) = with(binding) {
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
                    val request = PinCodeManagerService().checkPinCode(customerId,code, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            errorTitle.isGone = true
                            TransitionManager.beginDelayedTransition(binding.root)
                            animateTextChange(infoTitle, "Придумайте новый PIN-код")
                            activity.activityType = "setPinCode"
                            activity.checkActivityType()
                            activity.code = ""
                            activity.oldPinCode = mCode
                            backspaceUI("", binding, context)
                        } else if (response?.ResultCode == 25) {
                            showViewWithAnimation(errorTitle)
                            TransitionManager.beginDelayedTransition(binding.root)
                            enableErrorBorder(firstNumberLayout, context)
                            enableErrorBorder(secondNumberLayout, context)
                            enableErrorBorder(thirdNumberLayout, context)
                            enableErrorBorder(fourthNumberLayout, context)
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ввести старый PIN-код ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                backspaceUI("", binding, context)
                            }
                            .show()
                    }
                }
            }
        }
    }
}