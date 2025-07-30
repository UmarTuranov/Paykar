package tj.paykar.shop.domain.usecase.wallet.functions

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View

fun addBlinkEffect(view: View) {
    val animator = ValueAnimator.ofFloat(0.3f, 1f)
    animator.addUpdateListener { valueAnimator ->
        val alpha = valueAnimator.animatedValue as Float
        view.alpha = alpha
    }
    animator.duration = 250
    animator.repeatCount = 0
    animator.start()
}