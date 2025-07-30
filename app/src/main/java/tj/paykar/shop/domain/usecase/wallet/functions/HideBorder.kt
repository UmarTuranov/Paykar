package tj.paykar.shop.domain.usecase.wallet.functions

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import androidx.core.content.ContextCompat
import tj.paykar.shop.R

fun hideBorder(view: View, context: Context) {
    val greyBorder: Drawable? = ContextCompat.getDrawable(context, R.drawable.border_grey)
    val greenBorder: Drawable? = ContextCompat.getDrawable(context, R.drawable.border_green)
    val drawables = arrayOf(greenBorder, greyBorder)
    val transitionDrawable = TransitionDrawable(drawables)
    view.background = transitionDrawable
    transitionDrawable.startTransition(300)
}