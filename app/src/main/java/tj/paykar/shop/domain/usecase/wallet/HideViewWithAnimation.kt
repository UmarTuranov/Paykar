package tj.paykar.shop.domain.usecase.wallet

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible

class HideViewWithAnimation {
    fun goneViewWithAnimation(view: View, duration: Long = 800) {
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        fadeOut.duration = duration
        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.isGone = true
            }
        })
        fadeOut.start()
    }

    fun invisibleViewWithAnimation(view: View, duration: Long = 800) {
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        fadeOut.duration = duration
        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.isInvisible = true
            }
        })
        fadeOut.start()
    }
}