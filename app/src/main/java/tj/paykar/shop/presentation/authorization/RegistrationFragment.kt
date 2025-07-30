package tj.paykar.shop.presentation.authorization

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tj.paykar.shop.data.AUTH
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentRegistrationBinding
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard

class RegistrationFragment : Fragment() {

    lateinit var binding: FragmentRegistrationBinding
    private var firstName = ""
    private var lastName = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(requireActivity(), view)
            false
        }

        binding.apply {
            val phone = UserStorageData(AUTH).getPhone()
            firstName = AUTH.loyaltyData?.firstName ?: ""
            lastName = AUTH.loyaltyData?.lastName ?: ""
            phoneEditText.text = phone.toEditable()
            firstNameText.text = firstName.toEditable()
            lastNameText.text = lastName.toEditable()
            registration.setOnClickListener {
                firstName = firstNameText.text.toString()
                lastName = lastNameText.text.toString()
                if (firstNameText.text.toString().length < 2) {
                    firstNameLayout.error = "Укажите корректное Имя"
                    TransitionManager.beginDelayedTransition(binding.root)
                } else if (lastNameText.text.toString().length < 2) {
                    lastNameLayout.error = "Укажите корректную Фамилию"
                    TransitionManager.beginDelayedTransition(binding.root)
                } else if (phoneEditText.text.isNullOrEmpty()) {
                    phoneLayout.error = "Укажите номер телефона"
                    TransitionManager.beginDelayedTransition(binding.root)
                } else if (phoneEditText.text.toString().length != 9) {
                    phoneLayout.error = "Укажите корректный номер телефона"
                    TransitionManager.beginDelayedTransition(binding.root)
                } else if (codeEditText.text.isNullOrEmpty()) {
                    codeLayout.error = "Укажите код подтверждение"
                    TransitionManager.beginDelayedTransition(binding.root)
                } else if (codeEditText.text.toString().length != 4) {
                    codeLayout.error = "Укажите корректный код подтверждение"
                    TransitionManager.beginDelayedTransition(binding.root)
                } else {
                    firstNameLayout.isErrorEnabled = false
                    lastNameLayout.isErrorEnabled = false
                    phoneLayout.isErrorEnabled = false
                    codeLayout.isErrorEnabled = false
                    TransitionManager.beginDelayedTransition(binding.root)
                    val confirmCode = codeEditText.text.toString()
                    AUTH.confirmLogin(firstName, lastName, confirmCode)
                }
            }

        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}