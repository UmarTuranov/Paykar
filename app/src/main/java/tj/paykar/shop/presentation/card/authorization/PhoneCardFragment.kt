package tj.paykar.shop.presentation.card.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.CARD
import tj.paykar.shop.data.storage.NullableHaveStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentPhoneCardBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.presentation.card.registration.CardRegistrationActivity

class PhoneCardFragment : Fragment() {

    lateinit var binding: FragmentPhoneCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPhoneCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnTouchListener{_, _ ->
            hideKeyboard(requireActivity(), view)
            false
        }

        binding.apply {
            nextAuth.setOnClickListener {
                NullableHaveStorage(CARD).removeNullableHave()
                val phone = phoneEditText.text.toString()
                if(phoneEditText.text.toString().length > 8) {
                    savingProcess.isVisible = true
                    nextAuth.isVisible = false
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val checkCard = CardManagerService().cardCheck(phone)
                            withContext(Dispatchers.Main) {
                                if (checkCard.isSuccessful) {
                                    Log.d("---D CheckNull", checkCard.body().toString())
                                    if (checkCard.body()?.LastName == null || checkCard.body()?.FirstName == null || checkCard.body()?.Birthday == null){
                                        NullableHaveStorage(CARD).setHaveNullable()
                                    }
                                    UserStorageData(CARD).saveCardPhone(phone)
                                    CARD.navController.navigate(R.id.action_phoneCardFragment_to_confirmCardFragment)
                                } else {
                                    savingProcess.isVisible = false
                                    nextAuth.isVisible = true
                                    MaterialAlertDialogBuilder(CARD)
                                        .setTitle("Ошибка!")
                                        .setMessage("Указанный Вами номер не привязан к клубной карте Пайкар. Попробуйте указать другой номер")
                                        .setPositiveButton("Попробовать еще") {_,_ ->}
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                savingProcess.isVisible = false
                                try {
                                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                                    snack.show()
                                }catch (_:Exception){}
                            }
                        }
                    }
                } else {
                    phoneLayout.error = "Укажите корректный номер телефона"
                }
            }
            registerCard.setOnClickListener {
                startActivity(Intent(activity, CardRegistrationActivity::class.java))
            }
        }
    }
}