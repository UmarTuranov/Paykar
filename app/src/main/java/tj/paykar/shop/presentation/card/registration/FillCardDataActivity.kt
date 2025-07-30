package tj.paykar.shop.presentation.card.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityFillCardDataBinding
import tj.paykar.shop.domain.usecase.shop.AddPointsRegistrationManagerService
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.presentation.card.authorization.BindCardActivity
import java.util.Calendar

class FillCardDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityFillCardDataBinding
    private var sendConfCode = false
    private var phoneConf = false
    private var cardCode = ""
    private var clientId: Int = 0
    private var userId: Int = 0
    private var isQRCode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFillCardDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        getPutIntent()
        setupView()
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        cardCode = bundle?.getString("cardCode") ?: ""
        clientId = bundle?.getInt("clientId") ?: 0
        isQRCode = bundle?.getBoolean("isQRCode") ?: false
        userId = UserStorageData(this).getUserId()
    }

    private fun setupView() {
        binding.apply {
            confPhoneLayout.isVisible = false
            phoneText.text = "+992".toEditable()

            val genderList = listOf("Мужской", "Женский")
            val genderAdapter = ArrayAdapter(this@FillCardDataActivity, R.layout.list_item, genderList)
            (genderLayout.editText as? AutoCompleteTextView)?.setAdapter(genderAdapter)

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            var dayWith0 = ""
            var monthWith0 = ""
            var birthDate = ""
            val maxYear = year - 16

            birthdayLayout.setEndIconOnClickListener {
                val datePicker = DatePickerDialog(this@FillCardDataActivity, { _, myear, mmonth, mday ->
                    dayWith0 = if (mday<10){
                        "0$mday"
                    }else{
                        "$mday"
                    }
                    monthWith0 = if (mmonth<9){
                        "0${mmonth + 1}"
                    }else{
                        "${mmonth + 1}"
                    }
                    birthDate = "$myear-$monthWith0-${dayWith0}T00:00:00"
                    birthdayText.text = "$dayWith0.$monthWith0.$myear".toEditable()
                }, maxYear, 11, 31)

                val maxDate = Calendar.getInstance()
                maxDate.set(maxYear, 11, 31)
                datePicker.datePicker.maxDate = maxDate.timeInMillis
                datePicker.show()
            }

            confPhone.setOnClickListener {
                confPhoneLayout.isVisible = true

                if (!sendConfCode) {

                    if (phoneText.text?.length == 13){
                        phoneLayout.isEnabled = false
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                CardManagerService().sendSmsCode(phoneText.text.toString(), cardCode)
                                withContext(Dispatchers.Main) {
                                    confPhone.text = "Подтвердить код"
                                    sendConfCode = true
                                }
                            } catch (e: Exception) {
                                Log.d("--E Exception", e.toString())
                                withContext(Dispatchers.Main){
                                    phoneLayout.isEnabled = true
                                    MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                        .setTitle("Сервер недоступен!")
                                        .setMessage("Не удалось отправить код, попробуйте позже")
                                        .setPositiveButton("Продолжить") {_,_ ->}
                                        .show()
                                }
                            }
                        }
                    }else{
                        phoneLayout.error = "Укажите корректный номер телефона"
                    }

                } else {

                    loading.isVisible = true
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val check = CardManagerService().checkSmsCode(phoneText.text.toString(), cardCode, confPhoneText.text.toString())
                            withContext(Dispatchers.Main) {
                                loading.isVisible = false
                                if (check) {

                                    phoneConf = true
                                    confPhoneLayout.isVisible = false
                                    confPhone.isVisible = false

                                    MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                        .setTitle("Успешно подтверждено!")
                                        .setMessage("Ваш номер успешно подтвержден")
                                        .setPositiveButton("Продолжить") {_,_ ->}
                                        .show()
                                }else{
                                    MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                        .setTitle("Неправильный код подтверждения!")
                                        .setMessage("Попробуйте еще раз")
                                        .setPositiveButton("Попробовать еще") {_,_ ->}
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("--E Exception", e.toString())
                            withContext(Dispatchers.Main){
                                loading.isVisible = false
                                MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                    .setTitle("Сервер недоступен!")
                                    .setMessage("Попробуйте позже")
                                    .setPositiveButton("Продолжить") {_,_ ->}
                                    .show()
                            }
                        }
                    }

                }

            }

            saveBtn.setOnClickListener {

                val genderId = when(genderText.text.toString()){
                    "Мужской" -> 1
                    "Женский" -> 2
                    else -> 0
                }

                if (phoneConf){
                    if (firstNameText.text.toString() != "" && lastNameText.text.toString() != "" && birthDate != "" && genderText.text.toString() != ""){
                        loading.isVisible = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val save = CardManagerService().saveCardInfo(clientId,
                                    lastNameText.text.toString(), firstNameText.text.toString(),
                                    "", birthDate, phoneText.text.toString(),
                                    "", "", "", "", genderId, false)

                                if (save.isSuccessful) {
                                    AddPointsRegistrationManagerService().addPointsToClient(
                                        firstNameText.text.toString(),
                                        lastNameText.text.toString(),
                                        "Android",
                                        phoneText.text.toString().removeRange(0..3),
                                        cardCode,
                                        isQRCode
                                    )
                                }

                                withContext(Dispatchers.Main) {
                                    loading.isVisible = false
                                    if (save.isSuccessful){
                                        MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                            .setTitle("Зарегистрировано!")
                                            .setMessage("Ваша карта зарегистрирована, Вы можете привязать вашу карту")
                                            .setPositiveButton("Привязать карту") {_,_ ->
                                                startActivity(Intent(this@FillCardDataActivity, BindCardActivity::class.java))
                                            }
                                            .setCancelable(false)
                                            .show()
                                    }else{
                                        Log.d("--E Exception", save.toString())
                                        MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                            .setTitle("Сервер недоступен!")
                                            .setMessage("Попробуйте позже")
                                            .setPositiveButton("Продолжить") {_,_ ->}
                                            .show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main){
                                    loading.isVisible = false
                                    Log.d("--E Exception", e.toString())
                                    MaterialAlertDialogBuilder(this@FillCardDataActivity)
                                        .setTitle("Сервер недоступен!")
                                        .setMessage("Попробуйте позже")
                                        .setPositiveButton("Продолжить") {_,_ ->}
                                        .show()
                                }
                            }
                        }

                    }else{
                        firstNameLayout.error = "Обязательное поле"
                        lastNameLayout.error = "Обязательное поле"
                        birthdayLayout.error = "Обязательное поле"
                        genderLayout.error = "Обязательное поле"
                    }
                }else{
                    MaterialAlertDialogBuilder(this@FillCardDataActivity)
                        .setTitle("Номер телефона не подтвержден!")
                        .setMessage("Подтвердите ваш номер телефона прежде чем сохранить данные")
                        .setPositiveButton("Продолжить") {_,_ ->}
                        .show()
                }
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}