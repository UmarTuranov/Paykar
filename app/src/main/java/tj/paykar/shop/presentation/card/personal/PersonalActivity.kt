package tj.paykar.shop.presentation.card.personal

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityPersonalBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.card.VirtualCardActivity


class PersonalActivity : AppCompatActivity() {

    lateinit var binding: ActivityPersonalBinding

    private var card: UserCardStorageModel = UserCardStorageModel(0, "false","","","", "", "", "", "", "", "", "", "", "", false, false)

    private var isEnabled: Boolean = false
    private var phoneConf: Boolean = true
    private var sendConfCode: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonalBinding.inflate(layoutInflater)
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
        getUserData()
        setupView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this, VirtualCardActivity::class.java))
    }

    private fun getUserData() {
        card = UserStorageData(this).getInfoCard()
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        binding.apply {

            editInfomation.isVisible = false

            val colors = intArrayOf(
                resources.getColor(R.color.red),
                resources.getColor(R.color.green),
                resources.getColor(R.color.darkGreen),
                resources.getColor(R.color.shopGrey),
                resources.getColor(R.color.yellow)
            )

            val red = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[0]))
            val green = ColorStateList(arrayOf(IntArray(1)), intArrayOf(colors[1]))
            val darkGreen = ColorStateList(arrayOf(IntArray(2)), intArrayOf(colors[2]))

            confPhoneLayout.isVisible = false
            confPhone.isVisible = false
            cancelBtn.isVisible = false

            phoneConf = card.IsPhoneConfirmed ?: false

            if (!phoneConf) {
                phoneLayout.isErrorEnabled = true
                phoneLayout.setStartIconDrawable(R.drawable.ic_telephone)
                phoneLayout.setStartIconTintList(red)
                phoneLayout.error = getString(R.string.confPhoneError)
            } else { phoneLayout.isCounterEnabled = false }

            firstNameText.text = card.firstName?.toEditable()
            lastNameText.text = card.lastName?.toEditable()
            secondNameText.text = card.secondName?.toEditable()

            if (card.birthday != "") {
                val convertDate = MainManagerService().dateToConvert(card.birthday ?: "")
                birthDateText.text = convertDate.toEditable()
            }

            if(card.phone != "") {
                phoneText.text = card.phone?.toEditable()
            }

            exitButton.setOnClickListener { finish() }

            editInfomation.setOnClickListener {
                if (!isEnabled) {
                    isEnabled = true
                    firstNameLayout.isEnabled = true
                    lastNameLayout.isEnabled = true
                    secondNameLayout.isEnabled = true

                    //Проверить на изменения все поля

                    phoneLayout.isEnabled = true
                    phoneLayout.setStartIconDrawable(R.drawable.ic_telephone)
                    if (!phoneConf) {
                        phoneLayout.setStartIconTintList(red)
                        phoneLayout.isErrorEnabled = true
                        phoneLayout.error = getString(R.string.confPhoneError)
                        phoneLayout.isCounterEnabled = true
                        confPhone.isVisible = true
                    } else {
                        phoneText.setOnFocusChangeListener { _, _ ->
                            phoneConf = false
                            phoneLayout.setStartIconTintList(red)
                            phoneLayout.isErrorEnabled = true
                            phoneLayout.error = getString(R.string.confPhoneToError)
                            phoneLayout.isCounterEnabled = true
                            confPhone.isVisible = true
                        }
                    }
                    addressLayout.isEnabled = true
                    editInfomation.text = "Сохранить данные"
                    cancelBtn.isVisible = true
                } else {

                    CoroutineScope(Dispatchers.IO).launch {

                        try {

                            val save = CardManagerService().saveCardInfo(card.clientId,
                                lastNameText.text.toString(), firstNameText.text.toString(),
                                secondNameText.text.toString(), card.birthday ?: "", phoneText.text.toString(),
                                "", "", addressText.text.toString(), "", 0, true)

                            withContext(Dispatchers.Main) {
                                if (save.isSuccessful){

                                    val snack = Snackbar.make(root, "Ваши данные успешно сохранены!", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(this@PersonalActivity, R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                                    snack.setActionTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                                    //snack.setAction("Отменить") { }
                                    snack.show()

                                    isEnabled = false
                                    firstNameLayout.isEnabled = false
                                    lastNameLayout.isEnabled = false
                                    secondNameLayout.isEnabled = false
                                    phoneLayout.isEnabled = false
                                    phoneLayout.setStartIconDrawable(R.drawable.ic_checked)
                                    phoneLayout.isCounterEnabled = false
                                    addressLayout.isEnabled = false
                                    editInfomation.text = "Обновить данные"
                                    cancelBtn.isVisible = false
                                }

                            }
                        } catch (e: Exception) {
                            val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(this@PersonalActivity, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                            snack.show()
                        }
                    }
                }
            }

            confPhone.setOnClickListener {

                if (!phoneConf && !sendConfCode) {

                    confPhoneLayout.isVisible = true
                    confPhoneLayout.isEnabled = true
                    confPhoneLayout.setStartIconTintList(red)
                    confPhoneLayout.isErrorEnabled = true
                    confPhoneLayout.error = getString(R.string.confCodeError)
                    phoneLayout.isCounterEnabled = true

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            CardManagerService().sendSmsCode(phoneText.text.toString(), card.cardCode ?: "")
                            withContext(Dispatchers.Main) {
                                confPhone.text = "Подтвердить"
                                sendConfCode = true
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main){
                                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                snack.setBackgroundTint(ContextCompat.getColor(this@PersonalActivity, R.color.statusBarBackground))
                                snack.setTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                                snack.show()
                            }
                        }
                    }

                } else {

                    CoroutineScope(Dispatchers.IO).launch {

                        try {
                            val check = CardManagerService().checkSmsCode(phoneText.text.toString(), card.cardCode ?: "", confPhoneText.text.toString())
                            withContext(Dispatchers.Main) {

                                if (check) {

                                    phoneConf = true
                                    isEnabled = false

                                    val snack = Snackbar.make(root, "Ваш номер успешно подтвержден!", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(this@PersonalActivity, R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                                    snack.setActionTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                                    //snack.setAction("Отменить") { }
                                    snack.show()

                                    firstNameLayout.isEnabled = false
                                    lastNameLayout.isEnabled = false
                                    secondNameLayout.isEnabled = false
                                    phoneLayout.isEnabled = false
                                    phoneLayout.setStartIconDrawable(R.drawable.ic_checked)
                                    phoneLayout.setStartIconTintList(green)
                                    phoneLayout.error = ""
                                    phoneLayout.isEndIconVisible = false
                                    phoneLayout.isCounterEnabled = false
                                    confPhoneLayout.isVisible = false
                                    confPhoneLayout.isEnabled = false
                                    confPhoneLayout.error = ""
                                    confPhoneText.text = "".toEditable()
                                    confPhone.isVisible = false
                                    addressLayout.isEnabled = false
                                    editInfomation.text = "Обновить данные"
                                    cancelBtn.isVisible = false

                                }

                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main){
                                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                snack.setBackgroundTint(ContextCompat.getColor(this@PersonalActivity, R.color.statusBarBackground))
                                snack.setTextColor(ContextCompat.getColor(this@PersonalActivity, R.color.white))
                                snack.show()
                            }
                        }
                    }

                }

            }

            cancelBtn.setOnClickListener {
                isEnabled = false
                if (!phoneConf) {
                    phoneConf = true
                    phoneText.text = card.phone?.toEditable()
                    firstNameLayout.isEnabled = false
                    lastNameLayout.isEnabled = false
                    secondNameLayout.isEnabled = false
                    phoneLayout.isEnabled = false
                    phoneLayout.isErrorEnabled = false
                    phoneLayout.error = ""
                    phoneLayout.setStartIconDrawable(R.drawable.ic_checked)
                    phoneLayout.setStartIconTintList(green)
                    phoneLayout.isEndIconVisible = false
                    phoneLayout.isCounterEnabled = false
                    confPhoneLayout.isVisible = false
                    confPhoneLayout.isEnabled = false
                    confPhone.isVisible = false
                    addressLayout.isEnabled = false
                    editInfomation.text = "Обновить данные"
                    confPhone.text = "Получить код"
                    sendConfCode = false
                    cancelBtn.isVisible = false
                } else {
                    phoneConf = true
                    firstNameLayout.isEnabled = false
                    lastNameLayout.isEnabled = false
                    secondNameLayout.isEnabled = false
                    phoneLayout.isEnabled = false
                    phoneLayout.error = ""
                    phoneLayout.setStartIconDrawable(R.drawable.ic_checked)
                    phoneLayout.isCounterEnabled = false
                    addressLayout.isEnabled = false
                    confPhone.text = "Получить код"
                    sendConfCode = false
                    editInfomation.text = "Обновить данные"
                    cancelBtn.isVisible = false
                }
            }

        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}