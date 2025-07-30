package tj.paykar.shop.presentation.wallet.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityPersonalDataBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.presentation.wallet.HomeActivity
import java.util.Calendar

@Suppress("DEPRECATION")
class PersonalDataActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityPersonalDataBinding
    private var userGender: String = ""
    private var birthDate: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletActivityPersonalDataBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        setupView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        backBtn.setOnClickListener { onBackPressed() }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@PersonalDataActivity, currentFocus ?: View(this@PersonalDataActivity))
            false
        }

        val userBirthDate = UserStorage(this@PersonalDataActivity).getPaykarUserInfo()?.userData?.birth_date
        val formattedBirthDate = MainManagerService().convertDateFormat(userBirthDate ?: "", "yyyy-MM-dd", "dd.MM.yyyy")
        val birthDateEditable = MainManagerService().toEditable(formattedBirthDate)
        birthDateEditText.text = birthDateEditable
        birthDate = userBirthDate ?: ""

        showDatePicker(birthDateEditText) { selectedDate ->
            birthDate = selectedDate
        }

        val identificationStatus = UserStorage(this@PersonalDataActivity).getUserInfo()?.IdentificationRequest?.RequestState
        if (identificationStatus == 1) {
            val firstName = UserStorage(this@PersonalDataActivity).getUserInfo()?.Profile?.FirstName ?: ""
            val lastName = UserStorage(this@PersonalDataActivity).getUserInfo()?.Profile?.LastName ?: ""
            val gender = UserStorage(this@PersonalDataActivity).getUserInfo()?.Profile?.Gender
            val firstNameEditAble = MainManagerService().toEditable(firstName)
            val lastNameEditable = MainManagerService().toEditable(lastName)
            nameEditText.text = firstNameEditAble
            surnameEditText.text = lastNameEditable
            when (gender) {
                "M" -> {
                    maleRadioButton.isChecked = true
                    femaleRadioButton.isChecked = false
                    userGender = "Мужской"
                }
                "F" -> {
                    maleRadioButton.isChecked = false
                    femaleRadioButton.isChecked = true
                    userGender = "Женский"
                }
                else -> {
                    maleRadioButton.isChecked = false
                    femaleRadioButton.isChecked = false
                }
            }
            nameLayout.isEnabled = false
            surnameLayout.isEnabled = false
            maleRadioButton.isEnabled = false
            femaleRadioButton.isEnabled = false
            birthDateLayout.isEnabled = false
            saveBtn.isGone = true
        }
        else {
            val firstName = UserStorage(this@PersonalDataActivity).getPaykarUserInfo()?.userData?.firstname ?: ""
            val lastName = UserStorage(this@PersonalDataActivity).getPaykarUserInfo()?.userData?.lastname ?: ""
            val gender = UserStorage(this@PersonalDataActivity).getPaykarUserInfo()?.userData?.gender ?: ""
            val firstNameEditAble = MainManagerService().toEditable(firstName)
            val lastNameEditable = MainManagerService().toEditable(lastName)
            nameEditText.text = firstNameEditAble
            surnameEditText.text = lastNameEditable
            when (gender) {
                "Мужской" -> {
                    maleRadioButton.isChecked = true
                    femaleRadioButton.isChecked = false
                    userGender = "Мужской"
                }
                "Женский" -> {
                    maleRadioButton.isChecked = false
                    femaleRadioButton.isChecked = true
                    userGender = "Женский"
                }
                else -> {
                    maleRadioButton.isChecked = false
                    femaleRadioButton.isChecked = false
                }
            }

            maleRadioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    femaleRadioButton.isChecked = false
                    userGender = "Мужской"
                }
            }

            femaleRadioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    maleRadioButton.isChecked = false
                    userGender = "Женский"
                }
            }

            nameEditText.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrEmpty()) {
                    nameLayout.isErrorEnabled = false
                }
            }

            surnameEditText.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrEmpty()) {
                    surnameLayout.isErrorEnabled = false
                }
            }

            saveBtn.setOnClickListener {
                if (nameEditText.text.isNullOrEmpty()) {
                    nameLayout.error = "Обязательное поле"
                }
                else if (surnameEditText.text.isNullOrEmpty()) {
                    surnameLayout.error = "Обязательное поле"
                }
                else {
                    editUserRequest()
                }
            }
        }
    }

    private fun editUserRequest() = with(binding){
        val isOnline = MainManagerService().internetConnection(this@PersonalDataActivity)
        val progressDialog = CustomProgressDialog(this@PersonalDataActivity)
        val firstName = nameEditText.text.toString()
        val lastName = surnameEditText.text.toString()
        val phoneNumber = UserStorage(this@PersonalDataActivity).phoneNumber
        val balance = UserStorage(this@PersonalDataActivity).getUserInfo()?.Accounts?.find { it.AccountName == "Paykar Wallet" }?.Balance.toString()
        val deviceModel = DeviceInfo().getDeviceModel()
        val versionOS = Build.VERSION.RELEASE
        val ftoken = UserStorageData(this@PersonalDataActivity).getFirebaseToken()
        val imei = DeviceInfo().getDeviceIMEI(this@PersonalDataActivity)
        val ipAddress = IpAddressStorage(this@PersonalDataActivity).ipAddress ?: ""
        val shopUserId = UserStorageData(this@PersonalDataActivity).getUserId()
        val appVersion = BuildConfig.VERSION_NAME
        val typeOS = "Android"
        val walletUserId = UserStorage(this@PersonalDataActivity).getPaykarUserInfo()?.userData?.id
        if (isOnline) {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = UserManagerService().editWalletUserPaykar(
                        walletUserId ?: 0, firstName, lastName, userGender, phoneNumber ?: "",
                        birthDate ?: "", balance, deviceModel, typeOS, versionOS, ftoken,
                        appVersion, imei ?: "", ipAddress ?: "", shopUserId
                    )
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", request.body().toString())
                        if (request.isSuccessful) {
                            val response = request.body()
                            if (response?.status == "success") {
                                UserStorage(this@PersonalDataActivity).savePaykarUserInfo(response)
                                MaterialAlertDialogBuilder(this@PersonalDataActivity)
                                    .setTitle("Редактирование аккаунта")
                                    .setMessage("Ваши персональные данные были успешно обновлены")
                                    .setCancelable(false)
                                    .setPositiveButton("Понятно") {_, _ ->
                                        val intent = Intent(this@PersonalDataActivity, ProfileActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .show()
                            } else {
                                MaterialAlertDialogBuilder(this@PersonalDataActivity)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Что то пошло не так повторите попытку по позже")
                                    .setCancelable(false)
                                    .setPositiveButton("На главную") {_, _ ->
                                        val intent = Intent(this@PersonalDataActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .show()
                            }
                        }
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@PersonalDataActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Что то пошло не так повторите по попытку по позже!")
                            .setCancelable(false)
                            .setPositiveButton("На главную") {_, _ ->
                                val intent = Intent(this@PersonalDataActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .show()
                    }
                }
            }
        } else {
            MainManagerService().noInternetAlert(this@PersonalDataActivity)
        }
    }

    private fun showDatePicker(editText: TextInputEditText, callback: (String) -> Unit) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dayWith0: String
        var monthWith0: String
        editText.setOnClickListener {
            DatePickerDialog(this@PersonalDataActivity, { _, myear, mmonth, mday ->
                dayWith0 = if (mday < 10) {
                    "0$mday"
                } else {
                    "$mday"
                }
                monthWith0 = if (mmonth < 9) {
                    "0${mmonth + 1}"
                } else {
                    "${mmonth + 1}"
                }
                val date = "$myear-$monthWith0-$dayWith0"
                val birthDateEditable = MainManagerService().toEditable("$dayWith0.$monthWith0.$myear")
                editText.text = birthDateEditable
                callback(date)
            }, year, month, day).show()
        }
    }
}