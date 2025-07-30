package tj.paykar.shop.presentation.wallet.login

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
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.RequestDeviceTypeInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityRegistrationBinding
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.presentation.wallet.pincode.SetPinCodeActivity
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityRegistrationBinding
    private lateinit var progressDialog: CustomProgressDialog
    private var requestType: String = "registration"
    private var customerId: Int = 0
    private var birthDate: String? = ""
    private var notFoundUser: Boolean? = false
    private var notFoundPhone: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityRegistrationBinding.inflate(layoutInflater)
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
        progressDialog = CustomProgressDialog(this)
        getIntentData()
        setupView()
    }

    private fun removeCountryCode(phoneNumber: String): String {
        return if (phoneNumber.startsWith("+992")) {
            phoneNumber.replaceFirst("+992", "")
        } else if (phoneNumber.startsWith("992")) {
            phoneNumber.replaceFirst("992", "")
        } else {
            phoneNumber
        }
    }

    private fun getIntentData() {
        val bundle: Bundle? = intent.extras
        notFoundPhone = bundle?.getString("phoneNumber", "")
        notFoundUser = bundle?.getBoolean("notFound", false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        val userShopData = UserStorageData(this@RegistrationActivity).getUser()
        val cardUserData = UserStorageData(this@RegistrationActivity).getInfoCard()
        if (userShopData.id != 0 && cardUserData.clientId == 0) {
            val firstNameEditable = MainManagerService().toEditable(userShopData.firstName ?: "")
            val lastNameEditable = MainManagerService().toEditable(userShopData.lastName ?: "")
            val phoneEditable = MainManagerService().toEditable(removeCountryCode(userShopData.phone ?: ""))
            firstNameEditText.text = firstNameEditable
            lastNameEditText.text = lastNameEditable
            phoneEditText.text = phoneEditable
            Log.d("--D IsFirstIf", "YES")
        }
        else if (cardUserData.clientId != 0) {
            val firstNameEditable = MainManagerService().toEditable(cardUserData.firstName ?: "")
            val lastNameEditable = MainManagerService().toEditable(cardUserData.lastName ?: "")
            val birthDay = MainManagerService().convertDate(cardUserData.birthday ?: "")
            val birthDayEditable = MainManagerService().toEditable(birthDay)
            val phoneEditable = MainManagerService().toEditable(removeCountryCode(cardUserData.phone ?: ""))
            Log.d("--D BirthDate", cardUserData.birthday.toString())
            Log.d("--D Phone", "P${phoneEditable}P")
            phoneEditText.text = phoneEditable
            firstNameEditText.text = firstNameEditable
            lastNameEditText.text = lastNameEditable
            birthDateEditText.text = birthDayEditable
            birthDate = cardUserData.birthday
        }

        Log.d("--N NotFoundUser", notFoundUser.toString())
        Log.d("--N NotFoundPhone", notFoundPhone.toString())

        if (notFoundUser == true) {
            val phoneNumberEditable = MainManagerService().toEditable(notFoundPhone ?: "")
            phoneEditText.text = phoneNumberEditable
        }

        phoneEditText.doOnTextChanged { _, _, _, _ ->
            requestType = "registration"
            registrationBtn.text = "Далее"
            AnimateViewHeight().hideView(confirmCodeLayout)
            confirmCodeEditText.text?.clear()
        }

        showDatePicker(birthDateLayout, birthDateEditText) {
            birthDate = it
        }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@RegistrationActivity, currentFocus ?: View(this@RegistrationActivity))
            false
        }

        registrationBtn.setOnClickListener {
            if (requestType == "registration") {
                if (firstNameEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@RegistrationActivity)
                        .setTitle("Заполните поле ввода")
                        .setMessage("Введите имя для регистрации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                }
                else if (lastNameEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@RegistrationActivity)
                        .setTitle("Заполните поле ввода")
                        .setMessage("Введите фамилию для регистрации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                } else if (phoneEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@RegistrationActivity)
                        .setTitle("Номер телефона")
                        .setMessage("Введите номер телефона для регистрации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                } else {
                    Log.d("--B VersionName", BuildConfig.VERSION_NAME)
                    registrationRequest(phoneEditText.text.toString())
                }
            }
            else if (requestType == "confirmation") {
                if (firstNameEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@RegistrationActivity)
                        .setTitle("Заполните поле ввода")
                        .setMessage("Введите имя для регистрации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                }
                else if (lastNameEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@RegistrationActivity)
                        .setTitle("Заполните поле ввода")
                        .setMessage("Введите фамилию для регистрации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                }
                else if (confirmCodeEditText.text.isNullOrEmpty()) {
                    MaterialAlertDialogBuilder(this@RegistrationActivity)
                        .setTitle("Код подтверждения")
                        .setMessage("Введите код подтверждения для регистрации")
                        .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                        .show()
                }
                else {
                    confirmRegistrationRequest(confirmCodeEditText.text.toString())
                }
            }
        }
    }

    private fun registrationRequest(phoneNumber: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val deviceModel = DeviceInfo().getDeviceModel()
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = UserManagerService().createUser(phoneNumber, RequestDeviceTypeInfoModel(2, imei ?: "", BuildConfig.VERSION_NAME, deviceModel), RequestInfoModel("TJK", ipAddress, 3))
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        when (response?.ResultCode) {
                            0 -> {
                                customerId = response.CustomerId ?: 0
                                AnimateViewHeight().showView(binding.confirmCodeLayout, binding.confirmCodeLayout)
                                binding.registrationBtn.text = "Зарегистрироваться"
                                requestType = "confirmation"
                            }
                            2 -> {
                                MaterialAlertDialogBuilder(this@RegistrationActivity)
                                    .setTitle("Пользователь существует")
                                    .setMessage("Пользователь с номером $phoneNumber уже зарегистрирован")
                                    .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                            else -> {
                                requestResultCodeAlert(response?.ResultCode ?: 0, this@RegistrationActivity, response?.ResultDesc ?: "")
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@RegistrationActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {_, _ ->
                                registrationRequest(phoneNumber)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun savePaykarUser() = with(binding) {
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val phoneNumber = phoneEditText.text.toString()
        val deviceModel = DeviceInfo().getDeviceModel()
        val versionOS = Build.VERSION.RELEASE
        val ftoken = UserStorageData(this@RegistrationActivity).getFirebaseToken()
        val imei = DeviceInfo().getDeviceIMEI(this@RegistrationActivity)
        val ipAddress = IpAddressStorage(this@RegistrationActivity).ipAddress ?: ""
        val shopUserId = UserStorageData(this@RegistrationActivity).getUserId()
        val isOnline = MainManagerService().internetConnection(this@RegistrationActivity)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@RegistrationActivity)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = UserManagerService().createWalletUser(firstName, lastName, phoneNumber, birthDate ?: "Не указано", deviceModel, versionOS, ftoken, imei ?: "Не указано", ipAddress ?: "Не указано", BuildConfig.VERSION_NAME, shopUserId)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        val response = request.body()
                        if (response != null) {
                            UserStorage(this@RegistrationActivity).savePaykarUserInfo(response)
                        }
                        val intent = Intent(this@RegistrationActivity, SetPinCodeActivity::class.java)
                        intent.putExtra("requestType", "set")
                        startActivity(intent)
                        finish()
                        Animatoo.animateCard(this@RegistrationActivity)
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@RegistrationActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте по позже!")
                            .setCancelable(false)
                            .setPositiveButton("Понятно") {_, _ ->
                                val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun confirmRegistrationRequest(confirmCode: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val deviceModel = DeviceInfo().getDeviceModel()
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceTypeInfoModel(2, imei ?: "", BuildConfig.VERSION_NAME, deviceModel)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = UserManagerService().confirmRegistrationUser(customerId, confirmCode, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            savePaykarUser()
                            UserStorage(this@RegistrationActivity).saveUser(response.CustomerId?:0, binding.phoneEditText.text.toString(), response.DeviceInfo.Token ?: "")
                        } else {
                            progressDialog.dismiss()
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@RegistrationActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@RegistrationActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {_, _ ->
                                confirmRegistrationRequest(confirmCode)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun showDatePicker(inputLayout: TextInputLayout, editText: TextInputEditText, callback: (String) -> Unit) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dayWith0: String
        var monthWith0: String
        editText.setOnClickListener {
            DatePickerDialog(this@RegistrationActivity, { _, myear, mmonth, mday ->
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