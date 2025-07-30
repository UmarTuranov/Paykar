package tj.paykar.shop.domain.usecase.wallet.functions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.widget.TextView

fun animateTextChange(view: TextView, newText: String) {
    val scaleXOut = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
    val scaleYOut = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f)
    val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
    val setOut = AnimatorSet()
    setOut.playTogether(scaleXOut, scaleYOut, fadeOut)
    setOut.duration = 300
    setOut.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            view.text = newText
            val scaleXIn = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            val scaleYIn = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val setIn = AnimatorSet()
            setIn.playTogether(scaleXIn, scaleYIn, fadeIn)
            setIn.duration = 300
            setIn.start()
        }
    })
    setOut.start()
}