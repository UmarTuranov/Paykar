package tj.paykar.shop.presentation.authorization

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import tj.paykar.shop.R
import tj.paykar.shop.data.AUTH
import tj.paykar.shop.data.model.CheckSMSModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentConfirmationBinding
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard


class ConfirmationFragment : Fragment() {

    lateinit var binding: FragmentConfirmationBinding
    private var checkCode: CheckSMSModel = CheckSMSModel("")
    private var phone: String = ""
    private val maxSendsPerDay = 3
    private val dayInMillis = 24 * 60 * 60 * 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phone = UserStorageData(AUTH).getPhone()

        binding.root.setOnTouchListener{_, _ ->
            hideKeyboard(requireActivity(), view)
            false
        }

        binding.apply {
            sendSmsAgain.isEnabled = false
            sendSmsAgain.isClickable = false
            sendSmsAgain.setTextColor(ContextCompat.getColor(requireActivity(), R.color.shopGrey))
            nextConf.setOnClickListener {
                if(codeEditText.text.toString().length == 4) {
                    val confirmCode = codeEditText.text.toString()
                    AUTH.confirmLogin("", "", confirmCode)
                } else {
                    codeLayout.error = "Укажите корректный код"
                    TransitionManager.beginDelayedTransition(binding.root)
                }
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
