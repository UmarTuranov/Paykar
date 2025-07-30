package tj.paykar.shop.presentation.card.authorization

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import tj.paykar.shop.data.CARD
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentConfirmCardBinding
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard

class ConfirmCardFragment : Fragment() {

    lateinit var binding: FragmentConfirmCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConfirmCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(requireActivity(), view)
            false
        }

        binding.apply {
            val phone = UserStorageData(CARD).getCardPhone()
            CARD.sendSMS(phone)
            nextConf.setOnClickListener {
                if(codeEditText.text.toString().length > 3) {
                    CARD.checkConfCode(codeEditText.text.toString())
                } else {
                    codeLayout.error = "Укажите корректный код"
                }
            }
        }
    }

}