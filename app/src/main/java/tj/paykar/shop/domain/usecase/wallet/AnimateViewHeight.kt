package tj.paykar.shop.domain.usecase.wallet

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

class AnimateViewHeight {

    fun showView(cardView: View, view: View, duration: Long = 300) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = view.measuredHeight
        cardView.layoutParams.height = targetHeight
        cardView.visibility = View.VISIBLE
        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = cardView.layoutParams
            layoutParams.height = value
            cardView.layoutParams = layoutParams
        }
        animator.start()
    }

    fun hideView(cardView: View, duration: Long = 300) {
        val initialHeight = cardView.height
        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = cardView.layoutParams
            layoutParams.height = value
            cardView.layoutParams = layoutParams
            if (value == 0) {
                cardView.visibility = View.GONE
            }
        }
        animator.start()
    }

    fun showViewFixedHeight(cardView: View, height: Int, context: Context, duration: Long = 300) {
        val targetHeight = height
        cardView.layoutParams.height = 0
        cardView.visibility = View.VISIBLE
        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = cardView.layoutParams
            layoutParams.height = value
            cardView.layoutParams = layoutParams
        }
        animator.start()
    }
}