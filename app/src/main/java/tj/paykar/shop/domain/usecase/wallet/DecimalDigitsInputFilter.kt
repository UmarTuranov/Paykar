package tj.paykar.shop.domain.usecase.wallet

import android.text.InputFilter
import android.text.Spanned

class DecimalDigitsInputFilter(private val decimalDigits: Int) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val destText = dest.toString()
        val newText = destText.substring(0, dstart) + source.toString() + destText.substring(dend)

        if (newText.contains(".")) {
            val splitText = newText.split("\\.".toRegex()).toTypedArray()
            if (splitText.size > 1) {
                if (splitText[1].length > decimalDigits) {
                    return ""
                }
            }
        }
        return null
    }
}