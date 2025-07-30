package tj.paykar.shop.domain.usecase.wallet.functions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import tj.paykar.shop.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import tj.paykar.shop.databinding.WalletActivityPaymentBinding
import java.util.regex.Pattern

fun checkRegex(inputLayout: TextInputLayout, editText: TextInputEditText, regex: String, context: Context, binding: WalletActivityPaymentBinding, afterTextChanged: Boolean = true): Boolean {
    var isCorrect = true
    if (afterTextChanged) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable?) {
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(s.toString())
                if (s.isNullOrEmpty()) {
                    inputLayout.helperText = null
                    TransitionManager.beginDelayedTransition(binding.root)
                    binding.payBtn.isEnabled = false
                    isCorrect = false
                } else {
                    if (!matcher.matches() && regex.isNotEmpty()) {
                        inputLayout.helperText = "Некорректный формат ввода"
                        val red = ColorStateList.valueOf(context.resources.getColor(R.color.red))
                        inputLayout.setHelperTextColor(red)
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.payBtn.isEnabled = false
                    } else {
                        inputLayout.helperText = null
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.payBtn.isEnabled = true
                    }
                }
            }
        })
    } else {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(editText.text.toString())
        if (editText.text.isNullOrEmpty()) {
            inputLayout.helperText = null
            TransitionManager.beginDelayedTransition(binding.root)
            binding.payBtn.isEnabled = false
            isCorrect = false
        } else {
            if (!matcher.matches() && regex.isNotEmpty()) {
                inputLayout.helperText = "Некорректный формат ввода"
                val red = ColorStateList.valueOf(context.resources.getColor(R.color.red))
                inputLayout.setHelperTextColor(red)
                TransitionManager.beginDelayedTransition(binding.root)
                binding.payBtn.isEnabled = false
                isCorrect = false
            } else {
                inputLayout.helperText = null
                TransitionManager.beginDelayedTransition(binding.root)
                binding.payBtn.isEnabled = true
            }
        }
    }
    return isCorrect
}