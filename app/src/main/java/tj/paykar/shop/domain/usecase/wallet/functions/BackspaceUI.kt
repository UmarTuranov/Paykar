package tj.paykar.shop.domain.usecase.wallet.functions

import android.content.Context
import tj.paykar.shop.databinding.WalletActivitySetPinCodeBinding
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation

fun backspaceUI(mCode: String, binding: WalletActivitySetPinCodeBinding, context: Context) {
    binding.apply {
        when(mCode.length) {
            0 -> {
                showBorder(firstNumberLayout, context)
                hideBorder(secondNumberLayout, context)
                hideBorder(thirdNumberLayout, context)
                hideBorder(fourthNumberLayout, context)
                hidePinCodeIcon(firstNumberTitle)
                hidePinCodeIcon(secondNumberTitle)
                hidePinCodeIcon(thirdNumberTitle)
                hidePinCodeIcon(fourthNumberTitle)
            }
            1 -> {
                hideBorder(secondNumberLayout, context)
                hidePinCodeIcon(firstNumberTitle)
                showBorder(firstNumberLayout, context)
            }
            2 -> {
                hideBorder(thirdNumberLayout, context)
                hidePinCodeIcon(secondNumberTitle)
                showBorder(secondNumberLayout, context)
            }
            3 -> {
                hideBorder(fourthNumberLayout, context)
                hidePinCodeIcon(thirdNumberTitle)
                showBorder(thirdNumberLayout, context)
            }
            4 -> {
                hidePinCodeIcon(fourthNumberTitle)
                hideBorder(firstNumberLayout, context)
                hideBorder(secondNumberLayout, context)
                hideBorder(thirdNumberLayout, context)
                HideViewWithAnimation().invisibleViewWithAnimation(errorTitle)
            }
        }
    }
}