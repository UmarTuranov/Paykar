package tj.paykar.shop.domain.usecase.wallet.functions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator

fun hidePinCodeIcon(icon: View) {
    icon.post {
        val parent = icon.parent as View
        val animator = ObjectAnimator.ofFloat(icon, "translationY", parent.height.toFloat())
        animator.duration = 300
        animator.interpolator = AnticipateOvershootInterpolator()
        animator.start()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                icon.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
}