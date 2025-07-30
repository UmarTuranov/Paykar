package tj.paykar.shop.presentation.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.CheckSMSModel
import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.model.UserUpdateModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityProfileEditBinding
import tj.paykar.shop.domain.usecase.AuthorizationManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import java.util.*

class ProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileEditBinding
    private var user: UserStorageModel = UserStorageModel(0, "", "", "")
    private var userUpdateModel: UserUpdateModel = UserUpdateModel(0, "", "", "", "", "", "")
    private var isEnabled: Boolean = false
    private var sendConfCode: Boolean = false
    private var phoneConfirmed: Boolean = false
    private var myCode: CheckSMSModel = CheckSMSModel("")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
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

    private fun getUserData() {
        user = UserStorageData(this@ProfileEditActivity).getUser()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        binding.apply {

            val colors = intArrayOf(
                resources.getColor(R.color.red),
                resources.getColor(R.color.green),
                resources.getColor(R.color.darkGreen),
            )

            val red = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[0]))
            val green = ColorStateList(arrayOf(IntArray(1)), intArrayOf(colors[1]))

            confPhoneLayout.isVisible = false
            confPhone.isVisible = false
            cancelBtn.isVisible = false

            firstNameText.text = user.firstName?.toEditable()
            lastNameText.text = user.lastName?.toEditable()
            try {
                phoneText.text = "${user.phone?.get(3)}${user.phone?.get(4)}${user.phone?.get(5)}${user.phone?.get(6)}${user.phone?.get(7)}${user.phone?.get(8)}${user.phone?.get(9)}${user.phone?.get(10)}${user.phone?.get(11)}".toEditable()
            }catch (e: Exception){}
            birthDateText.text = UserStorageData(this@ProfileEditActivity).userBirthday.toEditable()

            exitButton.setOnClickListener { finish() }
            cancelBtn.setOnClickListener{ finish() }

            //---Календарь
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            birthDateLayout.setEndIconOnClickListener{
                DatePickerDialog(this@ProfileEditActivity, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                    val dayWith0 = if (mday<10){
                        "0$mday"
                    }else{
                        "$mday"
                    }
                    val monthWith0 = if (mmonth<9){
                        "0${mmonth + 1}"
                    }else{
                        "${mmonth + 1}"
                    }
                    birthDateText.text = "$dayWith0.$monthWith0.$myear".toEditable()
                }, year, month, day).show()
            }


            //---Обновить
            editInformation.setOnClickListener{
                if (!isEnabled){
                    firstNameLayout.isEnabled = true
                    lastNameLayout.isEnabled = true
                    birthDateLayout.isEnabled = true
                    phoneLayout.isEnabled = true
                    phoneLayout.setStartIconDrawable(R.drawable.ic_telephone)
                    cancelBtn.isVisible = true

                    editInformation.text = "Сохранить данные"
                    isEnabled = true
                }else{
                    val isOnline = MainManagerService().internetConnection(this@ProfileEditActivity)
                    if (!isOnline){
                        MaterialAlertDialogBuilder(this@ProfileEditActivity)
                            .setTitle("Нет интернета!")
                            .setMessage("Проверьте подключение к сети интернет и повторите попытку")
                            .setPositiveButton("Продолжить"){_,_ -> }
                            .show()
                    }else{
                        if (phoneConfirmed || phoneText.text.toString() == user.phone){

                            saving.isVisible = true

                            userUpdateModel = UserUpdateModel(
                                user.id,
                                firstNameText.text.toString(),
                                lastNameText.text.toString(),
                                "",
                                "${phoneText.text}",
                                birthDateText.text.toString(),
                                "")

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val updateResult = AuthorizationManagerService().updateUser(userUpdateModel)

                                    withContext(Dispatchers.Main){
                                        saving.isVisible = false
                                        if (updateResult.body()?.successfully != null){
                                            UserStorageData(this@ProfileEditActivity).updateUser(userUpdateModel)
                                            firstNameLayout.isEnabled = false
                                            lastNameLayout.isEnabled = false
                                            birthDateLayout.isEnabled = false
                                            phoneLayout.isEnabled = false
                                            editInformation.text = "Обновить данные"
                                            isEnabled = false
                                        }else{
                                            MaterialAlertDialogBuilder(this@ProfileEditActivity)
                                                .setTitle("Ваши данные не были обновлены!")
                                                .setMessage("Ваши данные не были обновлены, повторите попытку позже")
                                                .setPositiveButton("Понятно"){_,_ -> }
                                                .show()
                                        }
                                    }
                                }catch (_: Exception){
                                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(this@ProfileEditActivity, R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(this@ProfileEditActivity, R.color.white))
                                    snack.show()
                                }
                            }
                        } else if (!phoneConfirmed && phoneText.text.toString() != user.phone){
                            saving.isVisible = false
                            MaterialAlertDialogBuilder(this@ProfileEditActivity)
                                .setTitle("Номер телефона не подтвержден!")
                                .setMessage("При изменении номера телефона необходимо подтвердить его")
                                .setPositiveButton("Продолжить"){_,_ -> }
                                .show()
                        }else{
                            saving.isVisible = false
                            MaterialAlertDialogBuilder(this@ProfileEditActivity)
                                .setTitle("Неизвестная ошибка!")
                                .setMessage("Обратитесь за помощью к отделу разработки")
                                .setPositiveButton("Продолжить"){_,_ -> }
                                .show()
                        }
                    }
                }
            }

            //---При нажатии на поле номера телефона
            phoneText.setOnFocusChangeListener { _, _ ->
                phoneConfirmed = false
                phoneLayout.setStartIconTintList(red)
                phoneLayout.isErrorEnabled = true
                phoneLayout.error = getString(R.string.confPhoneToError)
                phoneLayout.isCounterEnabled = true
                confPhone.isVisible = true
            }

            //---Получить код
            confPhone.setOnClickListener{
                if (!phoneConfirmed && !sendConfCode) {

                    confPhoneLayout.isVisible = true
                    confPhoneLayout.isEnabled = true
                    confPhoneLayout.setStartIconTintList(red)
                    confPhoneLayout.isErrorEnabled = true
                    confPhoneLayout.error = getString(R.string.confCodeError)
                    phoneLayout.isCounterEnabled = true

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            myCode = AuthorizationManagerService().sendSmsCode(phoneText.text.toString())
                            withContext(Dispatchers.Main) {
                                confPhone.text = "Подтвердить"
                                sendConfCode = true
                            }
                        }catch (_: Exception){
                            val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(this@ProfileEditActivity, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(this@ProfileEditActivity, R.color.white))
                            snack.show()
                        }
                    }

                } else {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(confPhone.windowToken, 0)
                    if (myCode.code == confPhoneText.text.toString()) {
                        phoneConfirmed = true
                        val snack = Snackbar.make(root, "Ваш номер успешно подтвержден!", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(this@ProfileEditActivity, R.color.statusBarBackground))
                        snack.setTextColor(ContextCompat.getColor(this@ProfileEditActivity, R.color.white))
                        snack.setActionTextColor(ContextCompat.getColor(this@ProfileEditActivity, R.color.white))
                        //snack.setAction("Отменить") { }
                        snack.show()

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
                    }else{
                        val snack = Snackbar.make(root, "Неправильный код подтверждения!", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(this@ProfileEditActivity, R.color.red))
                        snack.setTextColor(ContextCompat.getColor(this@ProfileEditActivity, R.color.white))
                        snack.setActionTextColor(ContextCompat.getColor(this@ProfileEditActivity, R.color.white))
                        //snack.setAction("Отменить") { }
                        snack.show()
                    }
                }
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}