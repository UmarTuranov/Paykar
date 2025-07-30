package tj.paykar.shop.presentation.wallet.payment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.R
import tj.paykar.shop.data.TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.data.model.wallet.Account
import tj.paykar.shop.data.model.wallet.Operation
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.ServiceParameters
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityPaymentBinding
import tj.paykar.shop.databinding.WalletBottomSheetAddSavedServiceBinding
import tj.paykar.shop.databinding.WalletBottomSheetConfirmPaymentBinding
import tj.paykar.shop.databinding.WalletBottomSheetConfirmedPaymentBinding
import tj.paykar.shop.databinding.WalletBottomSheetOperationInfoBinding
import tj.paykar.shop.databinding.WalletBottomSheetPaymentCardBinding
import tj.paykar.shop.domain.adapter.wallet.PaymentBankCardAdapter
import tj.paykar.shop.domain.adapter.wallet.RecentPaymentAdapter
import tj.paykar.shop.domain.adapter.wallet.RecentPaymentAmountAdapter
import tj.paykar.shop.domain.reprository.wallet.RecentPaymentAmountManager
import tj.paykar.shop.domain.reprository.wallet.RecentPaymentManager
import tj.paykar.shop.domain.reprository.wallet.SelectBankCardService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DecimalDigitsInputFilter
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.OperationManagerService
import tj.paykar.shop.domain.usecase.wallet.PaymentManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.checkRegex
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.wallet.HomeActivity
import kotlin.math.pow
import kotlin.math.round


@Suppress("DEPRECATION")
class PaymentActivity : AppCompatActivity(), SelectBankCardService, RecentPaymentManager, RecentPaymentAmountManager {
    private lateinit var binding: WalletActivityPaymentBinding
    private lateinit var bankCardsBottomSheet: BottomSheetDialog
    private lateinit var adapter: PaymentBankCardAdapter
    private lateinit var progressDialog: CustomProgressDialog
    private var selectedPaymentAccount: Account? = null
    private var serviceInfo: PaymentServiceInfoModel? = null
    private var serviceId: Int? = 0
    private var selectPaymentAccount: String? = ""
    private var clearAccount: String? = ""
    private var activityType: String? = ""
    private var checkId: Int = 0
    private var qrResult: String? = ""

    private var savedServiceAccount: String? = ""
    private var savedServiceAccount2: String? = ""
    private var savedServicePaymentSum: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityPaymentBinding.inflate(layoutInflater)
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
        binding.backBtnLoading.setOnClickListener {
            addBlinkEffect(it)
            onBackPressed()
        }
        getIntentData()
        getServiceInfo()
    }

    private fun getIntentData() {
        val bundle: Bundle? = intent.extras
        Log.d("--A AccountNumberPA", selectPaymentAccount.toString())
        val paykarWalletAccount = UserStorage(this).getUserInfo()?.Accounts?.find { it.AccountName == "Paykar Wallet" }?.Account
        selectPaymentAccount = bundle?.getString("selectPaymentAccount", paykarWalletAccount)
        clearAccount = bundle?.getString("clearAccount", "")
        serviceId = bundle?.getInt("serviceId")
        activityType = bundle?.getString("activityType")
        qrResult = bundle?.getString("result")

        if (activityType == "savedService") {
            savedServiceAccount = bundle?.getString("savedServiceAccount")
            savedServiceAccount2 = bundle?.getString("savedServiceAccount2")
            savedServicePaymentSum = bundle?.getDouble("savedServicePaymentSum")
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "UseCompatLoadingForColorStateLists")
    private fun setupView() = with(binding) {
        val recentAccountList = serviceInfo?.LastAccounts
        val recentAmountList = serviceInfo?.LastAmounts
        var bankCardList = ArrayList<Account>()
        val linkedBankCardList = UserStorage(this@PaymentActivity).getUserInfo()?.Accounts
        bankCardList.addAll(linkedBankCardList ?: emptyList())

        showViewWithAnimation(serviceName)
        serviceName.text = serviceInfo?.Name

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keyboardHeight = screenHeight - rect.bottom

            if (keyboardHeight > screenHeight * 0.15) {
                footerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = keyboardHeight
                }
                payBtn.translationY = 0F
                ObjectAnimator.ofFloat(payBtn, "translationY", -keyboardHeight.toFloat()).apply {
                    duration = 300
                    start()
                }
            } else {
                footerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 0
                }
                ObjectAnimator.ofFloat(payBtn, "translationY", 0F).apply {
                    duration = 300
                    start()
                }
            }
        }

        backBtn.setOnClickListener {
            addBlinkEffect(it)
            onBackPressed()
        }

        doOnTextChanged()

        bankCardsBottomSheet = BottomSheetDialog(this@PaymentActivity)
        val bottomSheetBinding =
            WalletBottomSheetPaymentCardBinding.inflate(LayoutInflater.from(this@PaymentActivity))
        val bottomSheetView = bottomSheetBinding.root
        bankCardsBottomSheet.setContentView(bottomSheetView)

        if (recentAccountList?.isEmpty() == true || recentAccountList == null) {
            recentAccountsRecycler.isGone = true
        } else {
            recentAccountsRecycler.adapter = RecentPaymentAdapter(recentAccountList, this@PaymentActivity, this@PaymentActivity)
        }

        if (recentAmountList?.isEmpty() == true || recentAmountList == null) {
            recentPaymentAmountRecycler.isGone = true
        } else {
            recentPaymentAmountRecycler.adapter = RecentPaymentAmountAdapter(recentAmountList, this@PaymentActivity, this@PaymentActivity)
        }

        if(!clearAccount.isNullOrEmpty()) {
            bankCardList = bankCardList.filter { it.Account != clearAccount } as ArrayList<Account>
        }

        when (activityType) {
            "replenishMyPaykarWallet" -> {
                val phone = UserStorage(this@PaymentActivity).phoneNumber
                val phoneEditable = MainManagerService().toEditable(phone ?: "")
                accountEditText.text = phoneEditable
                accountLayout.isGone = true
                bankCardList = bankCardList.filter { it.AccountName != "Paykar Wallet" } as ArrayList<Account>
                bottomSheetBinding.infoTitle.text = "Привязанные карты"
                checkOperationRequest(phone ?: "", "")
                recentAccountsRecycler.isGone = true

                TransitionManager.beginDelayedTransition(linearLayout)
            }
            "replenishBankCard" -> {

            }
            "qrPayment" -> {
                val qrAccount = MainManagerService().toEditable(qrResult ?: "")
                accountEditText.text = qrAccount
                accountEditText.isEnabled = false
                accountLayout.isGone = true
                recentAccountsRecycler.isGone = true
                TransitionManager.beginDelayedTransition(linearLayout)
                Log.d("--RequestInfo", "Check Qr Code Request")
            }
            "savedService" -> {
                accountEditText.text = MainManagerService().toEditable(savedServiceAccount ?: "")
                if (!savedServiceAccount2.isNullOrEmpty()) {
                    account2EditText.text = MainManagerService().toEditable(savedServiceAccount2 ?: "")
                }
                try {
                    paymentSumEditText.text = MainManagerService().toEditable(savedServicePaymentSum.toString().replace("0.0", ""))
                } catch (_: Exception) {}

                checkOperationRequest(savedServiceAccount ?: "", savedServiceAccount2 ?: "")
            }
        }

        if (serviceId == TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID) {
            infoTitle.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.green)))
        }

        adapter = PaymentBankCardAdapter(bankCardList, this@PaymentActivity, selectPaymentAccount ?: "", this@PaymentActivity)
        selectedPaymentAccount = adapter.selectedAccount

        bankCardCard.setOnClickListener {
            bankCardsBottomSheet.show()
        }
        bottomSheetBinding.bankCardsRecycler.adapter = adapter
        bankCardNameTitle.text = adapter.selectedAccount.AccountName
        val balance = adapter.selectedAccount.Balance.toString().replace(".", ",")
        bankCardBalanceTitle.text = "$balance с"
        if (adapter.selectedAccount.Type == 1) {
            binding.cardImage.setImageResource(R.drawable.plogo_svg)
            binding.cardImage.imageTintList = ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.white))
            binding.bankCardCard.setCardBackgroundColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.paykar)))
            binding.bankCardNameTitle.setTextColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.white)))
            binding.bankCardBalanceTitle.setTextColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.white)))
            binding.icon.imageTintList = ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.white))
            showViewWithAnimation(binding.bankCardCard)
        } else if (adapter.selectedAccount.Type == 2){
            binding.cardImage.setImageResource(R.drawable.logo_korti_milli)
            binding.cardImage.imageTintList = null
            binding.cardImage.setPadding(0, 0, 0, 0)
            binding.bankCardCard.setCardBackgroundColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.lightToDark)))
            binding.bankCardNameTitle.setTextColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.blackToWhite)))
            binding.bankCardBalanceTitle.setTextColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.blackToWhite)))
            binding.icon.imageTintList = ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.blackToWhite))
            showViewWithAnimation(binding.bankCardCard)
        } else if (adapter.selectedAccount.Type == 5) {
            binding.cardImage.setImageResource(R.drawable.visa)
            binding.cardImage.imageTintList = null
            binding.cardImage.setPadding(0, 0, 0, 0)
            binding.bankCardCard.setCardBackgroundColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.lightToDark)))
            binding.bankCardNameTitle.setTextColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.blackToWhite)))
            binding.bankCardBalanceTitle.setTextColor(ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.blackToWhite)))
            binding.icon.imageTintList = ColorStateList.valueOf(this@PaymentActivity.resources.getColor(R.color.blackToWhite))
            showViewWithAnimation(binding.bankCardCard)
        }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@PaymentActivity, currentFocus ?: View(this@PaymentActivity))
            false
        }

        serviceName.setOnClickListener {
            if(serviceName.lineCount == 2) {
                MaterialAlertDialogBuilder(this@PaymentActivity)
                    .setMessage(serviceInfo?.Name)
                    .show()
            }
        }

        scroll.setOnTouchListener { _, _ ->
            hideKeyboard(this@PaymentActivity, currentFocus ?: View(this@PaymentActivity))
            false
        }

        payBtn.setOnClickListener {
            if (checkId == 0) {
                paymentSumEditText.requestFocus()
            } else {
                val commissionData =
                    serviceInfo?.CommissionProfiles?.find { it.AccountType == selectedPaymentAccount?.Type }
                if (commissionData != null) {
                    Log.d("--C CheckId", checkId.toString())
                    Log.d("--S SelectedAccount", selectedPaymentAccount.toString())
                    Log.d("--D Data P", adapter.selectedAccount.AccountName.toString())
                    if (accountEditText.text.isNullOrEmpty()) {
                        accountLayout.isErrorEnabled = true
                        accountLayout.error = "Обязательное поле"
                    } else if (paymentSumEditText.text.isNullOrEmpty()) {
                        paymentSumLayout.helperText = "Обязательное поле"
                    } else {
                        val totalAmount = ((paymentSumEditText.text.toString().toDouble() * commissionData.CommissionDetails.Rate!!) / 100 ) + paymentSumEditText.text.toString().toDouble()
                        if (totalAmount > (serviceInfo?.MaxAmount ?: 0.0F)) {
                            paymentSumLayout.helperText = "Максимальная сумма платежа: ${serviceInfo?.MaxAmount} с"
                        } else if (totalAmount < (serviceInfo?.MinAmount ?: 0F)) {
                            paymentSumLayout.helperText = "Минимальная сумма платежа: ${serviceInfo?.MinAmount} с"
                        }
                        else {
                            accountLayout.isErrorEnabled = false
                            paymentSumLayout.helperText = null
                            try {
                                val totalAmountRounded =  roundToDecimalPlaces(totalAmount)
                                showOperationInfoBottomSheet(totalAmountRounded, serviceInfo?.Name ?: "", accountEditText.text.toString(), commissionData.CommissionDetails.Name ?: "", selectedPaymentAccount?.AccountName ?: "", commentEditText.text.toString())
                            } catch (_: Exception) {}
                        }
                    }
                }
            }
        }
    }

    private fun roundToDecimalPlaces(value: Double): Double {
        val factor = 10.0.pow(2)
        return round(value * factor) / factor
    }

    private fun setupTextFields(mResponse: Response<PaymentServiceInfoModel>) = with(binding) {
        val response = mResponse.body()
        val account = response?.ServiceParameters?.find { it.ParamName == "Account" }
        val account2 = response?.ServiceParameters?.find { it.ParamName == "Account2" }
        val comment = response?.ServiceParameters?.find { it.ParamName == "Comment" }
        val description = response?.ServiceParameters?.find { it.ParamName == "Description" }

        fun setupTextField(layout: TextInputLayout, editText: TextInputEditText, param: ServiceParameters?, checkRegex: Boolean = true) {
            layout.hint = param?.ParamProperties?.Message
            param?.ParamProperties?.MaxLength?.let { maxLength ->
                editText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            }
            editText.inputType = when (param?.ParamProperties?.Type) {
                "text" -> InputType.TYPE_CLASS_TEXT
                "number" -> InputType.TYPE_CLASS_NUMBER
                else -> editText.inputType
            }
            if (checkRegex) {
                checkRegex(layout, editText, param?.ParamProperties?.Regex ?: "", this@PaymentActivity, binding)
            }
        }

        setupTextField(accountLayout, accountEditText, account, false)

        if (comment != null) {
            commentLayout.isVisible = true
            setupTextField(commentLayout, commentEditText, comment)
        } else {
            commentLayout.isGone = true
            TransitionManager.beginDelayedTransition(linearLayout)
        }

        if (description != null) {
            descriptionLayout.isVisible = true
            setupTextField(descriptionLayout, descriptionEditText, description)
            TransitionManager.beginDelayedTransition(linearLayout)
        } else {
            descriptionLayout.isGone = true
        }

        if (account2 != null) {
            account2Layout.isVisible = true
            setupTextField(account2Layout, account2EditText, account2, false)
            TransitionManager.beginDelayedTransition(linearLayout)
        } else {
            account2Layout.isGone = true
        }

        if (commentLayout.isVisible) showViewWithAnimation(commentLayout, 300)
        if (accountLayout.isVisible) showViewWithAnimation(accountLayout, 300)

        when {
            accountEditText.text.isNullOrEmpty() -> {
                accountEditText.requestFocus()
                showKeyboard(accountEditText)
            }
            account2Layout.isVisible && account2EditText.text.isNullOrEmpty() -> {
                account2EditText.requestFocus()
                showKeyboard(account2EditText)
            }
            paymentSumEditText.text.isNullOrEmpty() -> {
                paymentSumEditText.requestFocus()
                showKeyboard(paymentSumEditText)
            }
        }

        if (account2 != null) {
            account2EditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !account2EditText.text.isNullOrEmpty()) {
                    val isCorrectValueAccount2 = checkRegex(account2Layout, account2EditText, account2.ParamProperties.Regex ?: "", this@PaymentActivity, binding, false)
                    val isCorrectValueAccount = checkRegex(accountLayout, accountEditText, account?.ParamProperties?.Regex ?: "", this@PaymentActivity, binding, false)
                    if (isCorrectValueAccount && isCorrectValueAccount2) {
                        checkOperationRequest(accountEditText.text.toString(), account2EditText.text.toString())
                    }
                } else {
                    infoCard.isGone = true
                    account2Layout.helperText = null
                    TransitionManager.beginDelayedTransition(binding.root)
                    checkId = 0
                }
            }
        }

        accountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !accountEditText.text.isNullOrEmpty()) {
                val isCorrectValueAccount = checkRegex(accountLayout, accountEditText, account?.ParamProperties?.Regex ?: "", this@PaymentActivity, binding, false)
                val isCorrectValueAccount2 = checkRegex(account2Layout, account2EditText, account2?.ParamProperties?.Regex ?: "", this@PaymentActivity, binding, false)
                if (account2 != null) {
                    if (isCorrectValueAccount && isCorrectValueAccount2) {
                        checkOperationRequest(accountEditText.text.toString(), account2EditText.text.toString())
                    }
                } else {
                    if (isCorrectValueAccount) {
                        checkOperationRequest(accountEditText.text.toString(), account2EditText.text.toString())
                    }
                }
            } else {
                infoCard.isGone = true
                accountLayout.helperText = null
                TransitionManager.beginDelayedTransition(binding.root)
                checkId = 0
            }
        }

        paymentSumEditText.filters = arrayOf(DecimalDigitsInputFilter(2))
    }

    private fun doOnTextChanged() = with(binding) {
        accountEditText.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                accountLayout.isErrorEnabled = false
            }
        }

        paymentSumEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString() ?: ""
                if (input == ".") {
                    paymentSumEditText.setText("0.")
                    paymentSumEditText.setSelection(paymentSumEditText.text?.length ?: 0)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        paymentSumEditText.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty() && text.toString() != ".") {
                paymentSumLayout.helperText = null
                if (text.toString().toDouble() > (serviceInfo?.MaxAmount ?: 0.0F)) {
                    paymentSumLayout.helperText = "Максимальная сумма платежа: ${serviceInfo?.MaxAmount} с"
                } else if (text.toString().toDouble() < (serviceInfo?.MinAmount ?: 0F)) {
                    paymentSumLayout.helperText = "Минимальная сумма платежа: ${serviceInfo?.MinAmount} с"
                } else {
                    paymentSumLayout.helperText = null
                }
            } else if (text.isNullOrEmpty()) {
                paymentSumLayout.helperText = null
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showConfirmedPaymentBottomSheet(operationInfo: Operation) {
        val bottomSheetDialog = BottomSheetDialog(this@PaymentActivity)
        val bottomSheetBinding =
            WalletBottomSheetConfirmedPaymentBinding.inflate(LayoutInflater.from(this@PaymentActivity))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.behavior.isHideable = false
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
        val mPaymentSum = operationInfo.TotalAmount ?: 0.0
        val serviceName = operationInfo.ServiceName ?: ""
        val createDate = operationInfo.OperationDate ?: ""
        val receiver = operationInfo.Recipient ?: ""
        val commission = operationInfo.Comission ?: 0.0
        val paymentMethod = operationInfo.PaymentMethod ?: ""
        val comment = operationInfo.Comment ?: ""
        bottomSheetBinding.apply {
            if (activityType == "qrPayment") {
                saveService.isGone = true
            }

            paymentSum.text = "$mPaymentSum с"
            serviceNameTitle.text = serviceName
            dateTimeTitle.text = MainManagerService().dateConvert(createDate)
            receiverTitle.text = receiver
            commissionTitle.text = "$commission с"
            paymentTitle.text = paymentMethod

            if (comment.isEmpty()) {
                commentaryLinear.isGone = true
            } else {
                commentaryTitle.text = comment
            }

            mainActivityBtn.setOnClickListener {
                val intent = Intent(this@PaymentActivity, HomeActivity::class.java)
                intent.putExtra("updateBalance", true)
                startActivity(intent)
                finish()
                bottomSheetDialog.dismiss()
            }

            val existingSavedService = SavedServicesStorage(this@PaymentActivity).existingService(serviceId ?: 0, binding.accountEditText.text.toString())

            if (existingSavedService) {
                bottomSheetBinding.saveIcon.setImageResource(R.drawable.ic_save_filled)
                bottomSheetBinding.saveTitle.text = "Удалить из избранных"
                bottomSheetBinding.saveService.isEnabled = false
                bottomSheetBinding.saveService.isClickable = false
            }

            saveService.setOnClickListener {
                val existingService = SavedServicesStorage(this@PaymentActivity).existingService(serviceId ?: 0, binding.accountEditText.text.toString())
                addBlinkEffect(it)

                if (existingService) {
                    SavedServicesStorage(this@PaymentActivity).deleteService(serviceId ?: 0, binding.accountEditText.text.toString())
                    saveIcon.setImageResource(R.drawable.ic_save)
                    saveTitle.text = "Добавить в избранную"
                    showViewWithAnimation(saveService)
                } else {
                    val saveServiceSheet = BottomSheetDialog(this@PaymentActivity)
                    val saveServiceSheetBinding =
                        WalletBottomSheetAddSavedServiceBinding.inflate(LayoutInflater.from(this@PaymentActivity))
                    val saveServiceView = saveServiceSheetBinding.root
                    saveServiceSheet.setContentView(saveServiceView)
                    saveServiceSheet.show()

                    saveServiceSheetBinding.apply {
                        saveButton.setOnClickListener {
                            if (nameEditText.text.isNullOrEmpty()) {
                                nameLayout.error = "Обязательное поле"
                            } else {
                                nameLayout.isErrorEnabled = false
                                SavedServicesStorage(this@PaymentActivity).savedService(
                                    nameEditText.text.toString(),
                                    serviceId ?: 0,
                                    binding.accountEditText.text.toString(),
                                    binding.account2EditText.text.toString(),
                                    operationInfo.TotalAmount ?: 0.0,
                                    serviceInfo?.Icon ?: ""
                                )
                                bottomSheetBinding.saveIcon.setImageResource(R.drawable.ic_save_filled)
                                bottomSheetBinding.saveTitle.text = "Удалить из избранных"
                                saveServiceSheet.dismiss()
                                showViewWithAnimation(saveService)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showConfirmOperationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this@PaymentActivity)
        val bottomSheetBinding =
            WalletBottomSheetConfirmPaymentBinding.inflate(LayoutInflater.from(this@PaymentActivity))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            confirmButton.setOnClickListener {
                confirmOperation(checkId, confirmCodeEditText.text.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showOperationInfoBottomSheet(totalAmount: Double, serviceName: String, receiver: String, commission: String, paymentMethod: String, comment: String) {
        val bottomSheetDialog = BottomSheetDialog(this@PaymentActivity)
        val bottomSheetBinding =
            WalletBottomSheetOperationInfoBinding.inflate(LayoutInflater.from(this@PaymentActivity))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            if (activityType == "qrPayment") {
                receiverLinear.isGone = true
            }
            paymentSum.text = "${totalAmount.toString().replace(".", ",")} с"
            serviceNameTitle.text = serviceName
            receiverTitle.text = receiver
            commissionTitle.text = commission
            paymentTitle.text = paymentMethod
            commentaryTitle.text = comment

            commentaryTitle.setOnClickListener {
                Log.d("--S SelectedAccount", selectedPaymentAccount?.AccountName.toString())
            }

            if (comment.isEmpty()) {
                commentaryLinear.isGone = true
            }

            payButton.setOnClickListener {
                bottomSheetDialog.dismiss()
                if (activityType == "qrPayment") {
                    createQROperation(selectedPaymentAccount?.Account ?: "", checkId, binding.commentEditText.text.toString(), binding.paymentSumEditText.text.toString().toDouble())
                } else {
                    createOperation(
                        selectedPaymentAccount?.Account ?: "",
                        checkId,
                        binding.accountEditText.text.toString(),
                        binding.account2EditText.text.toString(),
                        binding.commentEditText.text.toString(),
                        binding.paymentSumEditText.text.toString().toDouble()
                    )
                }
            }
        }
        bottomSheetDialog.setOnDismissListener {
            binding.payBtn.isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkOperationRequest(account: String, account2: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        } else {
            showViewWithAnimation(binding.checkRequestAnimation)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = OperationManagerService().checkOperation(
                        customerId,
                        serviceId ?: 0,
                        account,
                        account2,
                        deviceInfo,
                        requestInfo
                    )
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.checkRequestAnimation)
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            binding.payBtn.isEnabled = true
                            checkId = response.CheckId ?: 0
                            Log.d("--C CheckId", checkId.toString())
                            val stringBuilder = StringBuilder()
                            Log.d("--TV Size", "${response.Params.size}:")
                            Log.d("--TV Array", "${response.Params}:")
                            Log.d("--TV ATitle", "${response.Params[0].Title}:")
                            Log.d("--TV AValue", "${response.Params[0].Value}:")
                            if (response.Params.isNotEmpty()) {
                                if (response.Params.size <= 1) {
                                    for (param in response.Params) {
                                        val title = param.Title?.trimEnd()
                                        val value = param.Value?.trimEnd()
                                        Log.d("--TV Title", "$title:")
                                        Log.d("--TV Value", "$value:")
                                        stringBuilder.append("$title: $value")
                                    }
                                } else {
                                    var cnt = 0
                                    for (param in response.Params) {
                                        cnt++
                                        val title = param.Title?.trimEnd()
                                        val value = param.Value?.trimEnd()
                                        Log.d("--TV Title", "$title:")
                                        Log.d("--TV Value", "$value:")
                                        if (cnt == response.Params.size) {
                                            stringBuilder.append("$title: $value")
                                        } else {
                                            stringBuilder.append("$title: $value\n\n")
                                        }
                                        Log.d("--P Param", stringBuilder.toString())
                                    }
                                }
                                binding.infoTitle.text = stringBuilder.toString()
                                binding.infoCard.isVisible = true
                                TransitionManager.beginDelayedTransition(binding.linearLayout)
                            }
                            binding.accountLayout.isErrorEnabled = false
                            binding.account2Layout.isErrorEnabled = false
                        }
                        else if (response?.ResultDesc == "Error result code response: WrongNumber") {
                            binding.accountLayout.error = "Некорректный счёт"
                            binding.accountLayout.isErrorEnabled = true
                            binding.account2Layout.error = "Некорректый счёт"
                            binding.account2Layout.isErrorEnabled = true
                            binding.infoCard.isGone = true
                            binding.payBtn.isEnabled = false
                            TransitionManager.beginDelayedTransition(binding.linearLayout)
                        } else if (response?.ResultCode == 9) {
                            binding.accountLayout.error = "Некорректный счёт"
                            binding.accountLayout.isErrorEnabled = true
                            binding.account2Layout.error = "Некорректый счёт"
                            binding.account2Layout.isErrorEnabled = true
                            binding.infoCard.isGone = true
                            binding.payBtn.isEnabled = false
                            TransitionManager.beginDelayedTransition(binding.linearLayout)
                        }
                        else {
                            binding.payBtn.isEnabled = false
                            requestResultCodeAlert(
                                response?.ResultCode ?: 0,
                                this@PaymentActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun checkQROperationRequest(account: String) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        } else {
            binding.payBtn.isEnabled = false
            showViewWithAnimation(binding.checkRequestAnimation)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = OperationManagerService().checkQrOperation(
                        customerId,
                        account,
                        deviceInfo,
                        requestInfo
                    )
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.checkRequestAnimation)
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            binding.payBtn.isEnabled = true
                            checkId = response.CheckId ?: 0
                            Log.d("--C CheckId", checkId.toString())
                            binding.payBtn.isEnabled = true
                            HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                            val stringBuilder = StringBuilder()
                            if (response.Params.isNotEmpty()) {
                                val finalParams = response.Params.filter { it.Name != "totalPrice" }
                                val totalPrice = response.Params.find { it.Name == "totalPrice" }
                                if (finalParams.size <= 1) {
                                    for (param in finalParams) {
                                        stringBuilder.append("${param.Title}: ${param.Value}")
                                    }
                                } else {
                                    for (param in finalParams) {
                                        stringBuilder.append("${param.Title}: ${param.Value}\n\n")
                                        Log.d("--P Param", stringBuilder.toString())
                                    }
                                }
                                binding.infoTitle.text = stringBuilder.toString()
                                binding.infoCard.isVisible = true
                                binding.paymentSumEditText.text = MainManagerService().toEditable(roundToDecimalPlaces(totalPrice?.Value?.toDouble() ?: 0.0).toString())
                                TransitionManager.beginDelayedTransition(binding.linearLayout)
                            }
                            binding.accountLayout.isErrorEnabled = false
                            binding.account2Layout.isErrorEnabled = false
                        }
                        else if (response?.ResultDesc == "Error result code response: WrongNumber") {
                            binding.payBtn.isEnabled = false
                            HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                            binding.infoTitle.text = "Некорректный счёт"
                        } else if (response?.ResultCode == 9) {
                            binding.payBtn.isEnabled = false
                            HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                            binding.infoTitle.text = "Некорректный счёт"
                        }
                        else {
                            binding.payBtn.isEnabled = false
                            HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                            requestResultCodeAlert(
                                response?.ResultCode ?: 0,
                                this@PaymentActivity,
                                response?.ResultDesc ?: ""
                            )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        HideViewWithAnimation().goneViewWithAnimation(binding.checkRequestAnimation)
                        HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard, 1000)
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@PaymentActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте по позже!")
                            .setCancelable(false)
                            .setPositiveButton("На главную") { _, _ ->
                                val intent = Intent(this@PaymentActivity, HomeActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun createOperation(customerAccount: String, checkId: Int, account: String, account2: String, comment: String, amount: Double) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            binding.payBtn.isEnabled = false
            progressDialog.show()
            Handler().postDelayed({
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                        val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                        val request = OperationManagerService().createOperation(customerId, customerAccount,
                            serviceId ?: 0, checkId,
                            account, account2,
                            comment, amount,
                            deviceInfo, requestInfo)
                        withContext(Dispatchers.Main) {
                            Log.d("--RequestInfo", "response: ${request.body()}")
                            val response = request.body()
                            when (response?.ResultCode) {
                                0 -> {
                                    getOperationInfo(response.OperId ?: 0)
                                }
                                23 -> {
                                    progressDialog.dismiss()
                                    showConfirmOperationBottomSheet()
                                }
                                else -> {
                                    progressDialog.dismiss()
                                    requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentActivity, response?.ResultDesc ?: "")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            Log.d("--E Exception", e.toString())
                            binding.payBtn.isEnabled = true
                            MaterialAlertDialogBuilder(this@PaymentActivity)
                                .setTitle("Произошла ошибка")
                                .setMessage("Попробуйте ещё раз!")
                                .setPositiveButton("Попробовать снова") {_, _ ->
                                    createOperation(customerAccount, checkId, account, account2, comment, amount)
                                }
                                .show()
                        }
                    }
                }
            }, 1000)
        }
    }

    private fun createQROperation(customerAccount: String, checkId: Int, comment: String, amount: Double) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        }
        else {
            binding.payBtn.isEnabled = false
            progressDialog.show()
            Handler().postDelayed({
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                        val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                        val request = OperationManagerService().createQrOperation(customerId, customerAccount,
                            checkId, comment,
                            amount, deviceInfo, requestInfo)
                        withContext(Dispatchers.Main) {
                            Log.d("--RequestInfo", "response: ${request.body()}")
                            val response = request.body()
                            if (response?.ResultCode == 0) {
                                getOperationInfo(response.OperId ?: 0)
                            } else {
                                progressDialog.dismiss()
                                requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentActivity, response?.ResultDesc ?: "")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            binding.payBtn.isEnabled = true
                            Log.d("--E Exception", e.toString())
                            MaterialAlertDialogBuilder(this@PaymentActivity)
                                .setTitle("Произошла ошибка")
                                .setMessage("Попробуйте ещё раз!")
                                .setPositiveButton("Попробовать снова") {_, _ ->
                                    createQROperation(customerAccount, checkId, comment, amount)
                                }
                                .show()
                        }
                    }
                }
            }, 1000)
        }
    }

    private fun confirmOperation(checkId: Int, confirmCode: String) {
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
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = OperationManagerService().confirmCreateOperation(customerId, checkId, confirmCode, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            getOperationInfo(response.OperId ?: 0)
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@PaymentActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") {_, _ ->
                                confirmOperation(checkId, confirmCode)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun getServiceInfo() {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        } else {
            Handler().postDelayed({
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                        val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                        val request = PaymentManagerService().getPaymentServiceInfo(
                            customerId,
                            serviceId ?: 0,
                            deviceInfo,
                            requestInfo
                        )
                        withContext(Dispatchers.Main) {
                            Log.d("--RequestInfo", "response: ${request.body()}")
                            val response = request.body()
                            if (response?.ResultCode == 0) {
                                serviceInfo = response
                                Glide.with(this@PaymentActivity)
                                    .load(response.Icon)
                                    .into(binding.serviceIcon)

                                setupView()
                                setupTextFields(request)
                                if (activityType == "qrPayment") {
                                    checkQROperationRequest(qrResult ?: "")
                                } else {
                                    HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                                }
                            } else {
                                HideViewWithAnimation().goneViewWithAnimation(binding.loadingCard)
                                binding.backBtn.setOnClickListener {
                                    onBackPressed()
                                }
                                requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentActivity, response?.ResultDesc ?: "")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            if (!isFinishing) {
                                Log.d("--E Exception", e.toString())
                                MaterialAlertDialogBuilder(this@PaymentActivity)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Попробуйте по позже!")
                                    .setCancelable(false)
                                    .setPositiveButton("На главную") { _, _ ->
                                        val intent = Intent(this@PaymentActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .show()
                            }
                        }
                    }
                }
            }, 500)
        }
    }

    private fun getOperationInfo(operationId: Int) {
        val ipAddress = IpAddressStorage(this).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this)
        val customerId = UserStorage(this).customerId ?: 0
        val token = UserStorage(this).token
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            MainManagerService().noInternetAlert(this)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = OperationManagerService().getOperationInfo(
                        customerId,
                        operationId,
                        deviceInfo,
                        requestInfo
                    )
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            val operationInfo = response.OperationInfo
                            showConfirmedPaymentBottomSheet(
                                operationInfo
                            )
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentActivity, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--E Exception", e.toString())
                        MaterialAlertDialogBuilder(this@PaymentActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Попробовать снова") { _, _ ->
                                getOperationInfo(operationId)
                            }
                            .show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSelectCard(isSelected: Boolean, selectedAccount: Account) {
        if (isSelected) {
            Log.d("--S SelectedAccount", selectedAccount.toString())
            bankCardsBottomSheet.dismiss()
            this.selectedPaymentAccount = selectedAccount
            binding.bankCardNameTitle.text = selectedAccount.AccountName
            val balance = selectedAccount.Balance.toString().replace(".", ",")
            binding.bankCardBalanceTitle.text = "$balance с"
            val padding = 8 * this.resources.displayMetrics.density.toInt()
            if (selectedAccount.Type == 1) {
                binding.cardImage.setImageResource(R.drawable.plogo_svg)
                binding.cardImage.imageTintList = ColorStateList.valueOf(this.resources.getColor(R.color.white))
                binding.cardImage.setPadding(padding, padding, padding, padding)
                binding.bankCardCard.setCardBackgroundColor(ColorStateList.valueOf(this.resources.getColor(R.color.paykar)))
                binding.bankCardNameTitle.setTextColor(ColorStateList.valueOf(this.resources.getColor(R.color.white)))
                binding.bankCardBalanceTitle.setTextColor(ColorStateList.valueOf(this.resources.getColor(R.color.white)))
                binding.icon.imageTintList = ColorStateList.valueOf(this.resources.getColor(R.color.white))
                showViewWithAnimation(binding.bankCardCard, 500)
            } else if (selectedAccount.Type == 2) {
                binding.bankCardBalanceTitle.isVisible = selectedAccount.ShowBalance == true
                binding.cardImage.setImageResource(R.drawable.logo_korti_milli)
                binding.cardImage.imageTintList = null
                binding.cardImage.setPadding(0, 0, 0, 0)
                binding.bankCardCard.setCardBackgroundColor(ColorStateList.valueOf(this.resources.getColor(R.color.lightToDark)))
                binding.bankCardNameTitle.setTextColor(ColorStateList.valueOf(this.resources.getColor(R.color.blackToWhite)))
                binding.bankCardBalanceTitle.setTextColor(ColorStateList.valueOf(this.resources.getColor(R.color.blackToWhite)))
                binding.icon.imageTintList = ColorStateList.valueOf(this.resources.getColor(R.color.blackToWhite))
                showViewWithAnimation(binding.bankCardCard, 500)
            } else if (selectedAccount.Type == 5) {
                binding.bankCardBalanceTitle.isVisible = selectedAccount.ShowBalance == true
                binding.cardImage.setImageResource(R.drawable.visa)
                binding.cardImage.imageTintList = null
                binding.cardImage.setPadding(0, 0, 0, 0)
                binding.bankCardCard.setCardBackgroundColor(ColorStateList.valueOf(this.resources.getColor(R.color.lightToDark)))
                binding.bankCardNameTitle.setTextColor(ColorStateList.valueOf(this.resources.getColor(R.color.blackToWhite)))
                binding.bankCardBalanceTitle.setTextColor(ColorStateList.valueOf(this.resources.getColor(R.color.blackToWhite)))
                binding.icon.imageTintList = ColorStateList.valueOf(this.resources.getColor(R.color.blackToWhite))
                showViewWithAnimation(binding.bankCardCard, 500)
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onClick(account: String) {
        if (binding.accountEditText.text.toString() != account && !binding.accountEditText.isFocused) {
            checkOperationRequest(account, binding.account2EditText.text.toString())
        }
        val accountEditable = MainManagerService().toEditable(account)
        binding.accountEditText.text = accountEditable
        binding.paymentSumEditText.requestFocus()
        binding.paymentSumEditText.setSelection(binding.paymentSumEditText.length())
        if (binding.paymentSumEditText.text.isNullOrEmpty()) {
            showKeyboard(binding.paymentSumEditText)
        }
    }

    override fun onClick(amount: Double) {
        val accountEditable = MainManagerService().toEditable(amount.toString())
        binding.paymentSumEditText.text = accountEditable
    }
}