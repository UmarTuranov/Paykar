package tj.paykar.shop.domain.usecase.wallet.functions

import android.animation.ObjectAnimator
import android.view.View
import androidx.core.view.isVisible

fun showViewWithAnimation(view: View, duration: Long = 800) {
    val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
    fadeIn.duration = duration
    fadeIn.start()
    view.isVisible = true
}