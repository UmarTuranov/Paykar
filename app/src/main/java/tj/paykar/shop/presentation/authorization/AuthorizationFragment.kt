package tj.paykar.shop.presentation.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.AUTH
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentAuthorizationBinding
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.webview.WebViewActivity


class AuthorizationFragment : Fragment() {

    lateinit var binding: FragmentAuthorizationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthorizationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(requireActivity(), view)
            false
        }

        binding.apply {
            nextAuth.isEnabled = offerPolicyCheckBox.isChecked

            loginLikeGuest.setOnClickListener {
                showViewWithAnimation(it, 400)
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            }

            nextAuth.setOnClickListener {
                val phone = phoneEditText.text.toString()
                if(phone.length == 9) {
                    UserStorageData(requireActivity()).savePhone(phone)
                    AUTH.checkLogin(phone)
                } else {
                    phoneLayout.error = "Укажите корректный номер телефона"
                    TransitionManager.beginDelayedTransition(binding.root)
                }
            }

            phoneEditText.doOnTextChanged { text, _, _, _ ->
                phoneLayout.isErrorEnabled = false
                nextAuth.isEnabled = offerPolicyCheckBox.isChecked && text.toString().length == 9
                TransitionManager.beginDelayedTransition(binding.root)
            }

            val tvTerms = binding.offerPolicyTitle
            val text = requireActivity().resources.getString(R.string.acceptOfferAndPolicy)
            val spannableString = SpannableString(text)
            val offerStart = text.indexOf("публичной оферты")
            val offerEnd = offerStart + "публичной оферты".length
            val offerSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shukr.tj/Content/media/oferta/Paykar_wallet.PDF"))
                    startActivity(intent)
                }
            }

            val policyStart = text.indexOf("политики конфиденциальности")
            val policyEnd = policyStart + "политики конфиденциальности".length
            val policySpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://paykar.tj/include/licenses_detail.php"))
                    startActivity(intent)
                }
            }

            val green = requireActivity().resources.getColor(R.color.green)
            spannableString.setSpan(offerSpan, offerStart, offerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(green), offerStart, offerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableString.setSpan(policySpan, policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(green), policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tvTerms.text = spannableString
            tvTerms.movementMethod = LinkMovementMethod.getInstance()

            offerPolicyCheckBox.setOnCheckedChangeListener { _, isChecked ->
                nextAuth.isEnabled = isChecked && phoneEditText.text.toString().length == 9
            }
        }

    }
}