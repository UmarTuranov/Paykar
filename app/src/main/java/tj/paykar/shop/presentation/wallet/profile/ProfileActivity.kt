package tj.paykar.shop.presentation.wallet.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import tj.paykar.shop.data.storage.wallet.SecurityStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityProfileBinding
import tj.paykar.shop.databinding.WalletBottomSheetSecurityBinding
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.wallet.IdentificationActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentHistoryActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentsListActivity
import tj.paykar.shop.presentation.wallet.pincode.SetPinCodeActivity
import tj.paykar.shop.presentation.wallet.qr.MyQrActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import eightbitlab.com.blurview.RenderScriptBlur
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.PaykarIdStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() = with(binding) {
        val windowBackground = window.decorView.background
        identificationBlurView.setupWith(binding.root, RenderScriptBlur(this@ProfileActivity))
            .setBlurRadius(25F)
            .setBlurAutoUpdate(true)
            .setFrameClearDrawable(windowBackground)
            .setOverlayColor(ContextCompat.getColor(this@ProfileActivity, R.color.transparentWhiteToBlack))
        identificationBlurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        identificationBlurView.setClipToOutline(true)

        qrBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@ProfileActivity, MyQrActivity::class.java)
            startActivity(intent)
        }

        paymentHistoryBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@ProfileActivity, PaymentHistoryActivity::class.java)
            startActivity(intent)
        }

        paymentsBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@ProfileActivity, PaymentsListActivity::class.java)
            startActivity(intent)
        }

        personalDataBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@ProfileActivity, PersonalDataActivity::class.java)
            startActivity(intent)
        }

        securityBtn.setOnClickListener {
            addBlinkEffect(it)
            showSecurityBottomSheet()
        }

        val phone = UserStorage(this@ProfileActivity).phoneNumber
        phoneNumberTitle.text = "+992$phone" ?: ""

        val identificationStatus = UserStorage(this@ProfileActivity).getUserInfo()?.IdentificationRequest?.RequestState
        if (identificationStatus == 0 || identificationStatus == 5) {
            identificationMainTitle.text = "Неидентифицированный"
            identificationDescTitle.text = "Пройдите идентификацию"
            val firstName = PaykarIdStorage(this@ProfileActivity).firstName ?: ""
            val lastName = PaykarIdStorage(this@ProfileActivity).lastName ?: ""
            userNameTitle.text = "$firstName $lastName"
        } else if (identificationStatus == 4) {
            identificationMainTitle.text = "Заявка на рассмотрении"
            identificationDescTitle.text = "Подождите пока вашу заявку примут"
            identificationIcon.isGone = true
            val firstName = PaykarIdStorage(this@ProfileActivity).firstName ?: ""
            val lastName = PaykarIdStorage(this@ProfileActivity).lastName ?: ""
            userNameTitle.text = "$firstName $lastName"
        } else if (identificationStatus == 1) {
            identificationMainTitle.text = "Идентифицированный"
            identificationDescTitle.text = "У вас максимальные лимиты"
            identificationIcon.isGone = true
            val firstName = UserStorage(this@ProfileActivity).getUserInfo()?.Profile?.FirstName ?: ""
            val lastName = UserStorage(this@ProfileActivity).getUserInfo()?.Profile?.LastName ?: ""
            userNameTitle.text = "$firstName $lastName"
        }

        identificationCard.setOnClickListener {
            if (identificationStatus == 0 || identificationStatus == 5) {
                addBlinkEffect(it)
                val intent = Intent(this@ProfileActivity, IdentificationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showSecurityBottomSheet() {
        val fingerPrintSetting = SecurityStorage(this).fingerPrintEnabled
        val hideBalanceEnabled = SecurityStorage(this).hideBalanceEnabled
        val bottomSheetDialog = BottomSheetDialog(this@ProfileActivity)
        val bottomSheetBinding = WalletBottomSheetSecurityBinding.inflate(LayoutInflater.from(this@ProfileActivity))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        checkBiometricSupportAndAuthenticate(bottomSheetBinding)
        bottomSheetBinding.apply {
            resetPinCodeLayout.setOnClickListener {
                val intent = Intent(this@ProfileActivity, SetPinCodeActivity::class.java)
                intent.putExtra("requestType", "reset")
                startActivity(intent)
                finish()
                bottomSheetDialog.dismiss()
            }

            infoTitle.setOnClickListener {
                Log.d("--F Finger", SecurityStorage(this@ProfileActivity).fingerPrintEnabled.toString())
            }

            if (fingerPrintSetting == true) {
                fingerPrintSettingSwitch.isChecked = true
            } else if (fingerPrintSetting == false) {
                fingerPrintSettingSwitch.isChecked = false
            }

            fingerPrintSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
                SecurityStorage(this@ProfileActivity).saveFingerPrintSetting(isChecked)
                Log.d("--F Finger", isChecked.toString())
            }

            if (hideBalanceEnabled == true) {
                hideBalanceSwitch.isChecked = true
            } else if (hideBalanceEnabled == false) {
                hideBalanceSwitch.isChecked = false
            }

            hideBalanceSwitch.setOnCheckedChangeListener { _, isChecked ->
                SecurityStorage(this@ProfileActivity).saveBalanceShow(isChecked)
            }
        }
    }

    private fun checkBiometricSupportAndAuthenticate(binding: WalletBottomSheetSecurityBinding) {
        val biometricManager = BiometricManager.from(this@ProfileActivity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.fingerPrintLayout.alpha = 1F
                binding.fingerPrintLayout.isEnabled = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.fingerPrintLayout.alpha = 0.5F
                binding.fingerPrintLayout.isEnabled = false
                binding.fingerPrintSettingSwitch.isChecked = false
                binding.fingerPrintSettingSwitch.isEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.fingerPrintLayout.alpha = 0.5F
                binding.fingerPrintLayout.isEnabled = false
                binding.fingerPrintSettingSwitch.isChecked = false
                binding.fingerPrintSettingSwitch.isEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.fingerPrintLayout.alpha = 0.5F
                binding.fingerPrintLayout.isEnabled = false
                binding.fingerPrintSettingSwitch.isChecked = false
                binding.fingerPrintSettingSwitch.isEnabled = false
            }
            else -> {
                binding.fingerPrintLayout.alpha = 0.5F
                binding.fingerPrintLayout.isEnabled = false
                binding.fingerPrintSettingSwitch.isChecked = false
                binding.fingerPrintSettingSwitch.isEnabled = false
            }
        }
    }
}