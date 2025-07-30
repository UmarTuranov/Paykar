package tj.paykar.shop.presentation.wallet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.CITY_PARKING_SERVICE_ID
import tj.paykar.shop.data.PUBLIC_UTILITIES_CATEGORY_ID
import tj.paykar.shop.data.MOBILE_OPERATORS_CATEGORY_ID
import tj.paykar.shop.data.WALLETS_CATEGORY_ID
import tj.paykar.shop.data.REPLENISH_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.data.TRANSFERS_CATEGORY_ID
import tj.paykar.shop.data.TRANSFER_TO_BANK_CARDS_PAYMENT_ID
import tj.paykar.shop.data.TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.data.model.wallet.BankCard
import tj.paykar.shop.data.model.wallet.ContactModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.data.storage.wallet.SecurityStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityHomeBinding
import tj.paykar.shop.databinding.WalletBottomSheetAddBankCardBinding
import tj.paykar.shop.databinding.WalletBottomSheetAddCardTypeBinding
import tj.paykar.shop.databinding.WalletBottomSheetContactsBinding
import tj.paykar.shop.databinding.WalletBottomSheetMySalaryBinding
import tj.paykar.shop.databinding.WalletBottomSheetSavedServicesBinding
import tj.paykar.shop.databinding.WalletBottomSheetSecurityBinding
import tj.paykar.shop.domain.adapter.wallet.BankCardAdapter
import tj.paykar.shop.domain.adapter.wallet.ContactsAdapter
import tj.paykar.shop.domain.adapter.wallet.SavedServicesAdapter
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.BankCardManagerService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.ExchangeRateManagerService
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentHistoryActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentsListActivity
import tj.paykar.shop.presentation.wallet.payment.SubPaymentsListActivity
import tj.paykar.shop.presentation.wallet.pincode.SetPinCodeActivity
import tj.paykar.shop.presentation.wallet.qr.MyQrActivity
import tj.paykar.shop.presentation.wallet.qr.QrScannerActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityHomeBinding
    private val appCon = this@HomeActivity
    private val linkedBankCardList = ArrayList<BankCard>()
    private val REQUEST_CONTACT_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = resources.getColor(R.color.whiteToBlack)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        window.navigationBarColor = this.resources.getColor(R.color.whiteToBlack)
        val identification = UserStorage(appCon).getUserInfo()?.IdentificationRequest?.RequestState
        if (identification == 1) {
            UserStorage(appCon).saveShowingIdentificationCardCount()
        }
        getBankCardList()
        setupView()
        getExchangeRate()
        setupBottomMenu()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() = with(binding) {
        swipeRefreshLayout.setOnRefreshListener {
            if (binding.main.currentState == binding.main.startState) {
                getUserInfo()
                getBankCardList()
            } else {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        val startOffset = 0
        val endOffset = 280
        swipeRefreshLayout.setProgressViewOffset(true, startOffset, endOffset)

        binding.main.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                Log.d("MotionLayout", "Transition started from $startId to $endId")
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                Log.d("--P Progress", progress.toString())
                val cornerRadiusStartProgress = 0.3F
                val cornerRadius: Float
                if (progress > cornerRadiusStartProgress) {
                    val adjustedProgress = (progress - cornerRadiusStartProgress) / (1f - cornerRadiusStartProgress)
                    cornerRadius = 0f + adjustedProgress * 90F
                } else {
                    cornerRadius = 0f
                }
                footerView.radius = cornerRadius
                Log.d("MotionLayout", "Transition in progress: $progress from $startId to $endId")
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                Log.d("MotionLayout", "Transition completed at $currentId")
                Log.d("MotionLayout", R.id.end.toString())
                if (currentId == R.id.start) {
                    nestedScrollView.smoothScrollTo(0, 0)
                }

                if (currentId == R.id.end) {
                    footerView.radius = 90F
                }
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                Log.d("MotionLayout", "Transition trigger: $triggerId with progress: $progress")
            }
        })

        val windowBackground = window.decorView.background
        blurView.setupWith(binding.root, RenderScriptBlur(this@HomeActivity))
            .setBlurRadius(20F)
            .setBlurAutoUpdate(true)
            .setFrameClearDrawable(windowBackground)
            .setOverlayColor(ContextCompat.getColor(this@HomeActivity, R.color.transparentWhiteToBlack))
        blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        blurView.setClipToOutline(true)


        identificationBlurView.setupWith(binding.root, RenderScriptBlur(this@HomeActivity))
            .setBlurRadius(20F)
            .setBlurAutoUpdate(true)
            .setFrameClearDrawable(windowBackground)
        identificationBlurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        identificationBlurView.setClipToOutline(true)

        paymentsBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(appCon, PaymentsListActivity::class.java)
            startActivity(intent)
        }

        replenishBtn.setOnClickListener {
            if (linkedBankCardList.isEmpty()) {
                MaterialAlertDialogBuilder(this@HomeActivity)
                    .setTitle("Привязанные карты")
                    .setMessage("У вас нет привязанных карт для пополнении Paykar Wallet")
                    .setPositiveButton("Привязать карту") { _, _ ->
                        showSelectCardTypeBottomSheet()
                    }
                    .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel()}
                    .show()
            } else {
                val intent = Intent(appCon, PaymentActivity::class.java)
                intent.putExtra("serviceId", REPLENISH_PAYKAR_WALLET_PAYMENT_ID)
                intent.putExtra("activityType", "replenishMyPaykarWallet")
                startActivity(intent)
            }
        }

        paymentHistoryBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(appCon, PaymentHistoryActivity::class.java)
            startActivity(intent)
        }

        qrBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(appCon, QrScannerActivity::class.java)
            startActivity(intent)
        }

        toPaykarWalletBtn.setOnClickListener {
//            checkContactPermissions()
            val intent = Intent(appCon, PaymentActivity::class.java)
            intent.putExtra("serviceId", TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID)
            intent.putExtra("serviceName", "На Paykar Wallet")
            startActivity(intent)
        }

        toBankCardBtn.setOnClickListener {
            val intent = Intent(appCon, PaymentActivity::class.java)
            intent.putExtra("serviceId", TRANSFER_TO_BANK_CARDS_PAYMENT_ID)
            intent.putExtra("serviceName", "Перевод на карту")
            startActivity(intent)
        }

        walletsButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SubPaymentsListActivity::class.java)
            intent.putExtra("categoryId", WALLETS_CATEGORY_ID)
            intent.putExtra("categoryName", "Кошельки")
            intent.putExtra("hasCategory", false)
            startActivity(intent)
        }

        mobileOperatorsButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SubPaymentsListActivity::class.java)
            intent.putExtra("categoryId", MOBILE_OPERATORS_CATEGORY_ID)
            intent.putExtra("categoryName", "Мобильные операторы")
            intent.putExtra("hasCategory", false)
            startActivity(intent)
        }

        publicUtilitiesButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SubPaymentsListActivity::class.java)
            intent.putExtra("categoryId", PUBLIC_UTILITIES_CATEGORY_ID)
            intent.putExtra("categoryName", "Мобильные операторы")
            intent.putExtra("hasCategory", false)
            startActivity(intent)
        }

        allServicesButton.setOnClickListener {
            val intent = Intent(appCon, PaymentsListActivity::class.java)
            startActivity(intent)
        }

        addBankCardBtn.setOnClickListener {
            showSelectCardTypeBottomSheet()
        }

        addBankCardTextButton.setOnClickListener {
            addBlinkEffect(it)
            showSelectCardTypeBottomSheet()
        }

        allServicesTextBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(appCon, PaymentsListActivity::class.java)
            startActivity(intent)
        }

        savedServicesBtn.setOnClickListener {
            showSavedServicesBottomSheet()
        }

        securityBtn.setOnClickListener {
            addBlinkEffect(it)
            showSecurityBottomSheet()
        }

        myQrButton.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@HomeActivity, MyQrActivity::class.java)
            startActivity(intent)
        }

        salaryCard.setOnClickListener {
            showSalaryBottomSheet()
        }

        transfersBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity, SubPaymentsListActivity::class.java)
            intent.putExtra("categoryId", TRANSFERS_CATEGORY_ID)
            intent.putExtra("categoryName", "Переводы")
            startActivity(intent)
        }

        cityParkingButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, PaymentActivity::class.java)
            intent.putExtra("serviceId", CITY_PARKING_SERVICE_ID)
            startActivity(intent)
        }

        val identificationStatus = UserStorage(this@HomeActivity).getUserInfo()?.IdentificationRequest?.RequestState
        when (identificationStatus) {
            1 -> {
                identificationStatusTitle.text = "Идентифицированный"
                identificationCard.isVisible = UserStorage(appCon).shouldShowIdentificationCard()
                identificationCard.strokeColor = this@HomeActivity.resources.getColor(R.color.green)
                identificationMainTitle.text = "Идентификация принята"
                identificationDescTitle.text = "У вас максимальные лимиты"
                val firstName = UserStorage(appCon).getUserInfo()?.Profile?.FirstName ?: ""
                userNameTitle.text = firstName
            }
            4 -> {
                identificationStatusTitle.isGone = true
                identificationCard.isVisible = true
                identificationMainTitle.text = "Заявка на рассмотрении"
                identificationDescTitle.text = "Подождите пока вашу заявку примут"
                identificationCard.strokeColor = this@HomeActivity.resources.getColor(R.color.borderGrey)
                val firstName = PaykarIdStorage(this@HomeActivity).firstName
                userNameTitle.text = firstName
            }
            else -> {
                val firstName = PaykarIdStorage(this@HomeActivity).firstName
                userNameTitle.text = firstName
                identificationCard.isVisible = true
                identificationCard.strokeColor = this@HomeActivity.resources.getColor(R.color.borderGrey)
                identificationCard.setOnClickListener {
                    val intent = Intent(this@HomeActivity, IdentificationActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        setupBalanceTitle()

        hideBalanceImageButton.setOnClickListener {
            val hideBalance = SecurityStorage(this@HomeActivity).hideBalanceEnabled
            if (hideBalance == true) {
                SecurityStorage(this@HomeActivity).saveBalanceShow(false)
                val walletAccount = UserStorage(appCon).getUserInfo()?.Accounts?.find { it.AccountName == "Paykar Wallet" }
                val balance = walletAccount?.Balance.toString().replace(".", ",").replace("null", "0,00")
                walletBalanceTitle.text = "$balance с"
                hideBalanceImageButton.setImageResource(R.drawable.ic_cancelled_eye)
            } else {
                SecurityStorage(this@HomeActivity).saveBalanceShow(true)
                walletBalanceTitle.text = "*****"
                hideBalanceImageButton.setImageResource(R.drawable.ic_eye)
            }
            showViewWithAnimation(it, 400)
            showViewWithAnimation(walletBalanceTitle, 400)
        }

        if (exchangeRatesLinear.isGone) {
            val layoutParams = linkedBankCardsLinear.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = (100 * this@HomeActivity.resources.displayMetrics.density).toInt()
            linkedBankCardsLinear.layoutParams = layoutParams
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupBalanceTitle() = with(binding) {
        val hideBalance = SecurityStorage(this@HomeActivity).hideBalanceEnabled
        if (hideBalance == true) {
            walletBalanceTitle.text = "*****"
            hideBalanceImageButton.setImageResource(R.drawable.ic_eye)
        } else {
            val walletAccount = UserStorage(appCon).getUserInfo()?.Accounts?.find { it.AccountName == "Paykar Wallet" }
            val balance = walletAccount?.Balance.toString().replace(".", ",").replace("null", "0,00")
            walletBalanceTitle.text = "$balance с"
            hideBalanceImageButton.setImageResource(R.drawable.ic_cancelled_eye)
        }
    }

    private fun showContactsBottomSheet() {
        val progressDialog = CustomProgressDialog(this)
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = WalletBottomSheetContactsBinding.inflate(LayoutInflater.from(this))
        bottomSheet.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.apply {
            progressDialog.show()
            lifecycleScope.launch(Dispatchers.IO) {
                var contacts = getAllContactInfo(applicationContext)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    contacts = contacts.filter { it.phone != null } as ArrayList<ContactModel>
                    val adapter = ContactsAdapter(contacts)
                    contactRecycler.adapter = adapter
                    bottomSheet.show()
                }
            }
        }
    }

    fun checkContactPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACT_PERMISSION
            )
        } else {
            showContactsBottomSheet()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACT_PERMISSION && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showContactsBottomSheet()
        }
    }

    private fun getAllContactInfo(context: Context): ArrayList<ContactModel> {
        val contentResolver = context.contentResolver
        val contacts = ArrayList<ContactModel>()

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0

                var phone: String? = null
                var email: String? = null
                var organization: String? = null
                var address: String? = null

                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id), null
                    )
                    phoneCursor?.use { pc ->
                        if (pc.moveToFirst()) {
                            phone = pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        }
                    }
                }

                val emailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                    arrayOf(id), null
                )
                emailCursor?.use { ec ->
                    if (ec.moveToFirst()) {
                        email = ec.getString(ec.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    }
                }

                val orgCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
                    null
                )
                orgCursor?.use { oc ->
                    if (oc.moveToFirst()) {
                        val company = oc.getString(oc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.COMPANY))
                        val title = oc.getString(oc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.TITLE))
                        organization = listOfNotNull(company, title).joinToString(", ")
                    }
                }

                val addressCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID} = ?",
                    arrayOf(id), null
                )
                addressCursor?.use { ac ->
                    if (ac.moveToFirst()) {
                        address = ac.getString(ac.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
                    }
                }

                contacts.add(ContactModel(phone, name, email, address, organization))
            }
        }

        return contacts
    }

    @SuppressLint("SetTextI18n")
    private fun showSecurityBottomSheet() {
        val fingerPrintSetting = SecurityStorage(this).fingerPrintEnabled
        val hideBalanceEnabled = SecurityStorage(this).hideBalanceEnabled
        val bottomSheetDialog = BottomSheetDialog(this@HomeActivity)
        val bottomSheetBinding = WalletBottomSheetSecurityBinding.inflate(LayoutInflater.from(this@HomeActivity))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        checkBiometricSupportAndAuthenticate(bottomSheetBinding)
        bottomSheetBinding.apply {
            resetPinCodeLayout.setOnClickListener {
                val intent = Intent(this@HomeActivity, SetPinCodeActivity::class.java)
                intent.putExtra("requestType", "reset")
                startActivity(intent)
                bottomSheetDialog.dismiss()
            }

            infoTitle.setOnClickListener {
                Log.d("--F Finger", SecurityStorage(this@HomeActivity).fingerPrintEnabled.toString())
            }

            if (fingerPrintSetting == true) {
                fingerPrintSettingSwitch.isChecked = true
            } else if (fingerPrintSetting == false) {
                fingerPrintSettingSwitch.isChecked = false
            }

            fingerPrintSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
                SecurityStorage(this@HomeActivity).saveFingerPrintSetting(isChecked)
                Log.d("--F Finger", isChecked.toString())
            }

            if (hideBalanceEnabled == true) {
                hideBalanceSwitch.isChecked = true
            } else if (hideBalanceEnabled == false) {
                hideBalanceSwitch.isChecked = false
            }

            hideBalanceSwitch.setOnCheckedChangeListener { _, isChecked ->
                SecurityStorage(this@HomeActivity).saveBalanceShow(isChecked)
                if (isChecked) {
                    binding.walletBalanceTitle.text = "*****"
                    binding.hideBalanceImageButton.setImageResource(R.drawable.ic_eye)
                    showViewWithAnimation(binding.hideBalanceImageButton, 400)
                    showViewWithAnimation(binding.walletBalanceTitle, 400)
                } else {
                    val walletAccount = UserStorage(appCon).getUserInfo()?.Accounts?.find { it.AccountName == "Paykar Wallet" }
                    val balance = walletAccount?.Balance.toString().replace(".", ",").replace("null", "0,00")
                    binding.walletBalanceTitle.text = "$balance с"
                    binding.hideBalanceImageButton.setImageResource(R.drawable.ic_cancelled_eye)
                    showViewWithAnimation(binding.hideBalanceImageButton, 400)
                    showViewWithAnimation(binding.walletBalanceTitle, 400)
                }
            }
        }
    }

    private fun showSalaryBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = WalletBottomSheetMySalaryBinding.inflate(LayoutInflater.from(this))
        bottomSheet.setContentView(bottomSheetBinding.root)
        val dialog = bottomSheet.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = dialog!!.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.layoutParams = layoutParams
        val behavior = BottomSheetBehavior.from(dialog)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.show()

        bottomSheetBinding.apply {
            cancelBtn.setOnClickListener {
                bottomSheet.dismiss()
            }
        }
    }

    private fun checkBiometricSupportAndAuthenticate(binding: WalletBottomSheetSecurityBinding) {
        val biometricManager = BiometricManager.from(this@HomeActivity)
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

    @SuppressLint("NotifyDataSetChanged")
    fun getBankCardList() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().bankCardList(customerId, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            linkedBankCardList.clear()
                            linkedBankCardList.addAll(response.Cards ?: emptyList())
                            if (response.Cards.isNullOrEmpty()) {
                                if (binding.addBankCardBtn.isGone) {
                                    AnimateViewHeight().showViewFixedHeight(binding.addBankCardBtn, 275, this@HomeActivity)
                                }
                                HideViewWithAnimation().invisibleViewWithAnimation(binding.addBankCardTextButton)
                                binding.bankCardsRecycler.isGone = true
                            } else {
                                val adapter = BankCardAdapter(response.Cards, this@HomeActivity)
                                binding.bankCardsRecycler.adapter = adapter
                                binding.bankCardsRecycler.setHasFixedSize(true)
                                adapter.notifyDataSetChanged()
                                AnimateViewHeight().hideView(binding.addBankCardBtn)
                                if (binding.addBankCardTextButton.isGone || binding.addBankCardTextButton.isInvisible) {
                                    showViewWithAnimation(binding.addBankCardTextButton)
                                }
                                showViewWithAnimation(binding.bankCardsRecycler)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        MaterialAlertDialogBuilder(this@HomeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                getBankCardList()
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun addBankCardRequest(cardType: Int) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val progressDialog = CustomProgressDialog(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().addBankCard(customerId, cardType, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            showAddBankBottomSheet(response.ConfirmUrl ?: "")
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@HomeActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@HomeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                addBankCardRequest(cardType)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun showSavedServicesBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = WalletBottomSheetSavedServicesBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            val servicesList = SavedServicesStorage(this@HomeActivity).getSavedServices()
            Log.d("--S ServiceList", servicesList.toString())
            if (servicesList.isEmpty()) {
                recyclerView.isGone = true
                emptyLinear.isVisible = true
            } else {
                val dialog = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                val layoutParams = dialog!!.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.layoutParams = layoutParams
                val behavior = BottomSheetBehavior.from(dialog)
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                behavior.halfExpandedRatio = 0.9F
                recyclerView.isVisible = true
                emptyLinear.isGone = true
                recyclerView.adapter = SavedServicesAdapter(servicesList, this@HomeActivity)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showAddBankBottomSheet(url: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = WalletBottomSheetAddBankCardBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setOnShowListener {
            val dialog = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val layoutParams = dialog!!.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.layoutParams = layoutParams
            val behavior = BottomSheetBehavior.from(dialog)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        bottomSheetDialog.show()
        bottomSheetBinding.backBtnHeaderView.setOnClickListener {
            addBlinkEffect(it)
            bottomSheetDialog.dismiss()
        }
        val webView = bottomSheetBinding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.setSupportZoom(true)
        webView.settings.defaultTextEncodingName = "utf-8"
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        webView.loadUrl(url)
    }

    private fun showSelectCardTypeBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = WalletBottomSheetAddCardTypeBinding.inflate(LayoutInflater.from(this))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetBinding.apply {
            cancelBtn.setOnClickListener {
                bottomSheet.dismiss()
            }

            kortiMilliCardButton.setOnClickListener {
                addBankCardRequest(1)
            }

            visaCardButton.setOnClickListener {
                addBankCardRequest(3)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUserInfo() = with(binding) {
        val ipAddress = IpAddressStorage(this@HomeActivity).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this@HomeActivity)
        val customerId = UserStorage(this@HomeActivity).customerId
        val token = UserStorage(this@HomeActivity).token
        Log.d("--RequestInfo", "token: $token")
        val isOnline = MainManagerService().internetConnection(this@HomeActivity)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this@HomeActivity)
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = UserManagerService().getUserInfo(customerId ?: 0, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            UserStorage(this@HomeActivity).saveUserInfo(response)
                            setupBalanceTitle()
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@HomeActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@HomeActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Что то пошло не так, попробуйте по позже!")
                            .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel() }
                            .show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getExchangeRate() = with(binding) {
        val isOnline = MainManagerService().internetConnection(appCon)
        if (isOnline) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = ExchangeRateManagerService().getExchangeRate()
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        usaRate.text = "${response?.usaRate?.rate} с"
                        euroRate.text = "${response?.euroRate?.rate} с"
                        russianRate.text = "${response?.russianRate?.rate} с"
                        if (exchangeRatesLinear.isGone) {
                            val layoutParams = linkedBankCardsLinear.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.bottomMargin = (0 * this@HomeActivity.resources.displayMetrics.density).toInt()
                            linkedBankCardsLinear.layoutParams = layoutParams
                            TransitionManager.beginDelayedTransition(binding.root)
                            exchangeRatesLinear.isVisible = true
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("--E Exception", e.toString())
                        if (exchangeRatesLinear.isVisible) {
                            AnimateViewHeight().hideView(exchangeRatesLinear)
                        }
                    }
                }
            }
        }
    }

    private fun setupBottomMenu() {
        val ptoken = PaykarIdStorage(this).userToken
        if (ptoken == "") {
            binding.bottomMenu.setMenuResource(R.menu.bottom_menu_not_authorized)
        } else {
            binding.bottomMenu.setMenuResource(R.menu.button_menu)
        }
        binding.bottomMenu.setItemSelected(R.id.wallet, true)
        binding.bottomMenu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)}
                R.id.catalog -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    startActivity(intent)
                }
                R.id.wallet -> {}
                R.id.card -> {
                    val intent = Intent(this, VirtualCardActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bundle: Bundle? = intent.extras
        var updateBalance: Boolean? = bundle?.getBoolean("updateBalance", false)
        if (updateBalance == true) {
            getUserInfo()
            updateBalance = false
        }
        setupView()
        setupBottomMenu()
    }
}