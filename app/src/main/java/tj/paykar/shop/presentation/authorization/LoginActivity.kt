package tj.paykar.shop.presentation.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.AUTH
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.model.id.CheckLoginLoyaltyModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.ActivityLoginBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.id.PaykarIdManagerService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import java.time.LocalDateTime

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var navController: NavController
    private lateinit var progressDialog: CustomProgressDialog
    var loyaltyData: CheckLoginLoyaltyModel? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
        setupNavigation()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    fun checkLogin(phone: String) {
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            tj.paykar.shop.domain.usecase.wallet.MainManagerService().noInternetAlert(this)
        } else {
            progressDialog.show()
            UserStorageData(this).savePhone(phone)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = PaykarIdManagerService().checkLogin(phone)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        if (request.isSuccessful) {
                            val response = request.body()
                            Log.d("--RequestInfo", response.toString())
                            if (response?.status == "successfully") {
                                val isRegistration = response.isRegistration
                                loyaltyData = response.loyalty
                                if (isRegistration == false) {
                                    AUTH.navController.navigate(R.id.action_authorizationFragment_to_confirmationFragment)
                                } else {
                                    AUTH.navController.navigate(R.id.action_authorizationFragment_to_registrationFragment)
                                }
                            } else if (response?.message == "Wait") {
                                MaterialAlertDialogBuilder(this@LoginActivity)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Действие временно недоступно. Попробуйте снова через минуту.")
                                    .setPositiveButton("Понятно") {_, _ ->}
                                    .show()
                            } else {
                                MaterialAlertDialogBuilder(this@LoginActivity)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Что то пошло не так повторите попытку по позже! Код ошибки: 500")
                                    .setPositiveButton("Понятно") {_, _ ->}
                                    .show()
                            }
                        }else {
                            MaterialAlertDialogBuilder(this@LoginActivity)
                                .setTitle("Произошла ошибка")
                                .setMessage("Сервер недоступен повторите по позже. Код ошибки: 501")
                                .setPositiveButton("Понятно") {_, _ ->}
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("--Exception", e.toString())
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@LoginActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Сервер недоступен повторите по позже. Код ошибки: 502")
                            .setPositiveButton("Понятно") {_, _ ->}
                            .show()
                    }
                }
            }
        }
    }

    fun confirmLogin(
        firstName: String,
        lastName: String,
        confirmCode: String
    ) {
        val phone = UserStorageData(this).getPhone()
        val deviceModel = DeviceInfo().getDeviceModel()
        val ipAddress = IpAddressStorage(this).ipAddress
        val ftoken = UserStorageData(this).getFirebaseToken()
        val imei = DeviceInfo().getDeviceIMEI(this)
        val typeOS = "Android"
        val versionOS = Build.VERSION.RELEASE
        val versionApp = BuildConfig.VERSION_NAME
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            tj.paykar.shop.domain.usecase.wallet.MainManagerService().noInternetAlert(this)
        } else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = PaykarIdManagerService().confirmLogin(
                        phone, firstName,
                        lastName,
                        deviceModel, typeOS,
                        versionOS, versionApp,
                        imei ?: "", "", ipAddress ?: "",
                        ftoken, confirmCode
                    )
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        if (request.isSuccessful) {
                            val response = request.body()
                            Log.d("--RequestInfo", response.toString())
                            if (response?.status == "Successfully") {
                                val dateNow = LocalDateTime.now().toString()
                                val shopUser = response.shop
                                val loyalty = response.loyalty
                                val wallet = response.wallet
                                val userInfo = response.userInfo

                                if (shopUser != null) {
                                    val saveUserModel = UserStorageModel(shopUser.userId ?: 0, shopUser.firstName, shopUser.lastName, shopUser.phone)
                                    UserStorageData(this@LoginActivity).saveUser(saveUserModel)
                                }

                                if (loyalty != null) {
                                    val fulNameSeparator = loyalty.Client?.FullName?.split(" ")
                                    val lFirstName = fulNameSeparator?.getOrNull(0) ?: ""
                                    val lLastName = fulNameSeparator?.getOrNull(1) ?: ""
                                    val lSecondName = fulNameSeparator?.getOrNull(2) ?: ""
                                    val shortCardCode = loyalty.Client?.Cards?.get(0)?.CardCode?.substring(6, 12)
                                    val saveLoyaltyModel = UserCardStorageModel(
                                        loyalty.Client?.AccountId ?: 0,
                                        "true",
                                        lFirstName,
                                        lLastName,
                                        lSecondName,
                                        loyalty.Client?.Gender ?: "",
                                        loyalty.Client?.Birthday ?: "",
                                        loyalty.Client?.PhoneMobile ?: "",
                                        loyalty.Token ?: "",
                                        "",
                                        loyalty.Client?.Cards?.get(0)?.Balance.toString(),
                                        loyalty.Client?.Cards?.get(0)?.CardCode ?: "",
                                        shortCardCode,
                                        dateNow,
                                        loyalty.Client?.Cards?.get(0)?.AccumulateOnly ?: false,
                                        true
                                    )
                                    UserStorageData(this@LoginActivity).saveInfoCard(saveLoyaltyModel)
                                }

                                if (wallet != null) {
                                    val customerId = wallet.CustomerId
                                    val token = wallet.Token
                                    UserStorage(this@LoginActivity).saveUser(customerId ?: 0, phone, token ?: "", wallet.isRegistration ?: false)
                                }

                                if (userInfo != null) {
                                    PaykarIdStorage(this@LoginActivity).saveUser(userInfo)
                                }

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            else if (response?.status == "error" && response.message == "Confirm loyalty code is wrong") {
                                MaterialAlertDialogBuilder(this@LoginActivity)
                                    .setTitle("Некорректный код")
                                    .setMessage("Укажите правильный код подтверждение")
                                    .setPositiveButton("Понятно"){_, _ ->}
                                    .show()
                            } else if (response?.status == "error" && response.message == "Confirm code is wrong") {
                                MaterialAlertDialogBuilder(this@LoginActivity)
                                    .setTitle("Некорректный код")
                                    .setMessage("Укажите правильный код подтверждение")
                                    .setPositiveButton("Понятно"){_, _ ->}
                                    .show()
                            }
                            else if (response?.status == "error" && response.message == "The session was not started correctly.") {
                                MaterialAlertDialogBuilder(this@LoginActivity)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Что то пошло не так, попробуйте ещё раз. Код ошибки: 520")
                                    .setPositiveButton("Понятно") {_, _ ->}
                                    .show()
                            } else {
                                MaterialAlertDialogBuilder(this@LoginActivity)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Что то пошло не так, попробуйте ещё раз. Код ошибки: 525")
                                    .setPositiveButton("Понятно") {_, _ ->}
                                    .show()
                            }
                        } else {
                            MaterialAlertDialogBuilder(this@LoginActivity)
                                .setTitle("Произошла ошибка")
                                .setMessage("Сервер недоступен повторите по позже. Код ошибки: 510")
                                .setPositiveButton("Понятно") {_, _ ->}
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("--Exception", e.toString())
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@LoginActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Сервер недоступен повторите по позже. Код ошибки: 505")
                            .setPositiveButton("Понятно") {_, _ ->}
                            .show()
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        navController = Navigation.findNavController(this, R.id.auth_nav)
        AUTH = this
    }

}