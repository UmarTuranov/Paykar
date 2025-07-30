package tj.paykar.shop.presentation.wallet.pincode

import android.content.Context
import tj.paykar.shop.databinding.WalletActivitySetPinCodeBinding
import tj.paykar.shop.domain.usecase.wallet.functions.backspaceUI
import tj.paykar.shop.domain.usecase.wallet.functions.hideBorder
import tj.paykar.shop.domain.usecase.wallet.functions.hidePinCodeIcon
import tj.paykar.shop.domain.usecase.wallet.functions.showBorder
import tj.paykar.shop.domain.usecase.wallet.functions.showPinCodeIcon

class InputUISetPinCode(private val mCode: String,
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

               android.os.Handler().postDelayed({
                   activity.activityType = "confirmSetPinCode"
                   activity.checkActivityType()
                   activity.code = ""
                   backspaceUI("", binding, context)
                   activity.typedCode = mCode
                }, 200)
            }

            else -> {}
        }
    }
}