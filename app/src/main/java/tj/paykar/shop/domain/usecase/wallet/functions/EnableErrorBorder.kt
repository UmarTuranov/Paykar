package tj.paykar.shop.domain.usecase.wallet.functions

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import tj.paykar.shop.R

fun enableErrorBorder(view: ConstraintLayout, context: Context) {
    val transitionDrawable = ContextCompat.getDrawable(context, R.drawable.transition_grey_red) as TransitionDrawable
    view.background = transitionDrawable
    transitionDrawable.startTransition(100)
}