package tj.paykar.shop.domain.usecase.wallet.functions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator

fun showPinCodeIcon(icon: View) {
    icon.post {
        val parent = icon.parent as View
        icon.translationY = parent.height.toFloat()
        icon.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(icon, "translationY", 0f)
        animator.duration = 300
        animator.interpolator = OvershootInterpolator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) { icon.visibility = View.VISIBLE }
            override fun onAnimationEnd(animation: Animator) { icon.visibility = View.VISIBLE }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.start()
    }
}