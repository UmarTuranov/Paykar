package tj.paykar.shop.presentation.wallet.payment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.Operation
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityPaymentHistoryBinding
import tj.paykar.shop.databinding.WalletBottomSheetFilterPaymentHistoryBinding
import tj.paykar.shop.domain.adapter.wallet.OperationsHistoryAdapter
import tj.paykar.shop.domain.usecase.wallet.DecimalDigitsInputFilter
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.OperationManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.hideKeyboard
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
class PaymentHistoryActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityPaymentHistoryBinding
    private var accountNumber: String? = ""
    private var customerId: Int = 0
    private var historyList = ArrayList<Operation>()
    private var isFiltered: Boolean = false
    private var incomeFilter: Boolean = false
    private var expenseFilter: Boolean = false
    private var paymentSumFilter: Boolean = false
    private var paymentSumFrom: Double? = null
    private var paymentSumTo: Double? = null
    private var filterDateFrom: String? = null
    private var filterDateTo: String? = null
    private var displayDateFrom: String? = null
    private var displayDateTo: String? = null
    private var filterDate: Boolean = false
    private var startDateMillis: Long? = null
    private var endDateMillis: Long? = null
    private var filteredList = ArrayList<Operation>()
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private lateinit var filterBottomSheet: BottomSheetDialog
    private lateinit var filterBottomSheetBinding: WalletBottomSheetFilterPaymentHistoryBinding
    private lateinit var adapter: OperationsHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityPaymentHistoryBinding.inflate(layoutInflater)
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
        adapter = OperationsHistoryAdapter(historyList, this)
        filterBottomSheet = BottomSheetDialog(this)
        filterBottomSheetBinding = WalletBottomSheetFilterPaymentHistoryBinding.inflate(LayoutInflater.from(this))
        filterBottomSheet.setContentView(filterBottomSheetBinding.root)
        filterDateFrom = LocalDateTime.now()
            .minusDays(30)
            .withHour(0)
            .withMinute(0)
            .withSecond(0).toString()
        filterDateTo = LocalDateTime.now()
            .plusDays(1)
            .withHour(23)
            .withMinute(59)
            .withSecond(59).toString()
        displayDateFrom = LocalDateTime.now().minusDays(30)
            .toLocalDate()
            .format(formatter)
        displayDateTo = LocalDateTime.now()
            .toLocalDate()
            .format(formatter)
        customerId = UserStorage(this@PaymentHistoryActivity).customerId ?: 0
        getIntentData()
        getHistory(true)
        setupView()
    }

    private fun getIntentData() {
        val bundle: Bundle? = intent.extras
        accountNumber = bundle?.getString("accountNumber", "")
    }

    private fun getHistory(showLoadingAnimation: Boolean) {
        if (!accountNumber.isNullOrEmpty()) {
            Log.d("--P CardHistory", "CardHistory")
            getBankCardPaymentHistory(accountNumber ?: "", showLoadingAnimation)
        } else {
            Log.d("--P CardHistory", "PaymentHistory")
            getPaymentHistory(showLoadingAnimation)
        }
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setupSearch(newText ?: "")
                return true
            }

        })

        historyRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (isFiltered) {
                    if (dy > 0) {
                        resetButton.hide()
                    } else if (dy < 0) {
                        resetButton.show()
                    }
                }
            }
        })

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@PaymentHistoryActivity, currentFocus ?: View(this@PaymentHistoryActivity))
            searchView.clearFocus()
            false
        }

        backBtn.setOnClickListener {
            addBlinkEffect(it)
            onBackPressed()
        }

        filterBtn.setOnClickListener {
            addBlinkEffect(it)
            showFilterBottomSheet()
        }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard(this@PaymentHistoryActivity, currentFocus ?: View(this@PaymentHistoryActivity))
            searchView.clearFocus()
            true
        }

//        historyRecycler.setOnTouchListener { _, _ ->
//            hideKeyboard(this@PaymentHistoryActivity, currentFocus ?: View(this@PaymentHistoryActivity))
//            searchView.clearFocus()
//            true
//        }

        resetButton.setOnClickListener {
            binding.resetButton.isGone = true
            isFiltered = false
            incomeFilter = false
            expenseFilter = false
            paymentSumFilter = false
            startDateMillis = null
            endDateMillis = null
            if (filterDate) {
                filterDateFrom = LocalDateTime.now()
                    .minusDays(30)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0).toString()
                filterDateTo = LocalDateTime.now()
                    .plusDays(1)
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59).toString()
                displayDateFrom = LocalDateTime.now().minusDays(30)
                    .toLocalDate()
                    .format(formatter)
                displayDateTo = LocalDateTime.now()
                    .toLocalDate()
                    .format(formatter)

                periodTitle.text = "$displayDateFrom - $displayDateTo"
                showViewWithAnimation(periodTitle)
                getHistory(true)
            } else {
                setAdapter()
                showViewWithAnimation(historyRecycler)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            getHistory(false)
        }

        val startOffset = 0
        val endOffset = 280
        swipeRefreshLayout.setProgressViewOffset(true, startOffset, endOffset)

        periodTitle.text = "$displayDateFrom - $displayDateTo"
    }

    private fun setupSearch(query: String) {
        val mFilteredList = ArrayList<Operation>()
        for (i in filteredList) {
            val recipient = i.Recipient?.lowercase(Locale.ROOT)
            val serviceName = i.ServiceName?.lowercase(Locale.ROOT)
            if (recipient?.contains(query.lowercase(Locale.ROOT)) == true || serviceName?.contains(query.lowercase(
                    Locale.ROOT)) == true) {
                mFilteredList.add(i)
            }
        }
        if (mFilteredList.isEmpty()) {
            adapter.setFilterList(emptyList())
        } else {
            adapter.setFilterList(mFilteredList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPaymentHistory(showLoadingAnimation: Boolean) = with(binding) {
        val ipAddress = IpAddressStorage(this@PaymentHistoryActivity).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this@PaymentHistoryActivity)
        val token = UserStorage(this@PaymentHistoryActivity).token
        Log.d("--RequestInfo", "token: $token")
        val isOnline = MainManagerService().internetConnection(this@PaymentHistoryActivity)
        if (!isOnline) {
            swipeRefreshLayout.isRefreshing = false
            MainManagerService().noInternetAlert(this@PaymentHistoryActivity)
        }
        else {
            if (loadingAnimation.isGone && showLoadingAnimation) {
                historyRecycler.isGone = true
                emptyLinear.isGone = true
                searchLayout.isGone = true
                showViewWithAnimation(loadingAnimation)
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = OperationManagerService().getOperationHistory(customerId, filterDateFrom ?: "", filterDateTo ?: "", deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        swipeRefreshLayout.isRefreshing = false
                        if (response?.ResultCode == 0) {
                            if (response.Operations.isEmpty()) {
                                showViewWithAnimation(emptyLinear)
                                HideViewWithAnimation().goneViewWithAnimation(historyRecycler)
                                HideViewWithAnimation().goneViewWithAnimation(loadingAnimation)
                            } else {
                                historyList.clear()
                                historyList.addAll(response.Operations)
                                filteredList.clear()
                                filteredList.addAll(response.Operations)
                                setAdapter()
                                HideViewWithAnimation().goneViewWithAnimation(emptyLinear)
                                HideViewWithAnimation().goneViewWithAnimation(loadingAnimation)
                                showViewWithAnimation(historyRecycler)
                                if (searchLayout.isGone) {
                                    showViewWithAnimation(searchLayout)
                                }
                            }
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentHistoryActivity, response?.ResultDesc ?: "")                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        swipeRefreshLayout.isRefreshing = false
                        MaterialAlertDialogBuilder(this@PaymentHistoryActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                            .show()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBankCardPaymentHistory(accountNumber: String, showLoadingAnimation: Boolean) = with(binding) {
        val ipAddress = IpAddressStorage(this@PaymentHistoryActivity).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this@PaymentHistoryActivity)
        val token = UserStorage(this@PaymentHistoryActivity).token
        val isOnline = MainManagerService().internetConnection(this@PaymentHistoryActivity)
        if (!isOnline) {
            swipeRefreshLayout.isRefreshing = false
            MainManagerService().noInternetAlert(this@PaymentHistoryActivity)
        }
        else {
            if (loadingAnimation.isGone && showLoadingAnimation) {
                historyRecycler.isGone = true
                emptyLinear.isGone = true
                searchLayout.isGone = true
                showViewWithAnimation(loadingAnimation)
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = OperationManagerService().getAccountOperationHistory(customerId, accountNumber, filterDateFrom ?: "", filterDateTo ?: "", deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        swipeRefreshLayout.isRefreshing = false
                        if (response?.ResultCode == 0) {
                            if (response.Operations.isEmpty()) {
                                showViewWithAnimation(emptyLinear)
                                HideViewWithAnimation().goneViewWithAnimation(historyRecycler)
                                HideViewWithAnimation().goneViewWithAnimation(loadingAnimation)
                            } else {
                                historyList.clear()
                                historyList.addAll(response.Operations)
                                filteredList.clear()
                                filteredList.addAll(response.Operations)
                                setAdapter()
                                HideViewWithAnimation().goneViewWithAnimation(emptyLinear)
                                HideViewWithAnimation().goneViewWithAnimation(loadingAnimation)
                                showViewWithAnimation(historyRecycler)
                                if (searchLayout.isGone) {
                                    showViewWithAnimation(searchLayout)
                                }
                            }
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, this@PaymentHistoryActivity, response?.ResultDesc ?: "")                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        swipeRefreshLayout.isRefreshing = false
                        MaterialAlertDialogBuilder(this@PaymentHistoryActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                            .show()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun showFilterBottomSheet() {
        filterBottomSheet.show()
        filterBottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        filterBottomSheet.setOnShowListener {
            filterDate = false
        }

        filterBottomSheetBinding.apply {
            periodEditText.setOnClickListener {
                val pickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTheme(R.style.RangeDatePickerStyle)
                    .setTitleText("Выберите период")
                if (startDateMillis == null || endDateMillis == null) {
                    pickerBuilder.setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                } else {
                    pickerBuilder.setSelection(
                        Pair(startDateMillis, endDateMillis)
                    )
                }
                val picker = pickerBuilder.build()
                picker.show(this@PaymentHistoryActivity.supportFragmentManager, "TAG")
                picker.addOnPositiveButtonClickListener { selection ->
                    startDateMillis = selection.first
                    endDateMillis = selection.second
                    val startDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(startDateMillis ?: 0L),
                        ZoneId.systemDefault()
                    ).withHour(0).withMinute(0).withSecond(0)

                    val endDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(endDateMillis ?: 0L),
                        ZoneId.systemDefault()
                    ).withHour(23).withMinute(59).withSecond(59)
                    filterDateFrom = startDate.toString()
                    filterDateTo = endDate.toString()
                    displayDateFrom = startDate.format(formatter)
                    displayDateTo = endDate.format(formatter)
                    periodEditText.text = MainManagerService().toEditable("от $displayDateFrom до $displayDateTo")
                    binding.periodTitle.text = "$displayDateFrom-$displayDateTo"
                    filterDate = true
                    showViewWithAnimation(periodLayout, 500)
                }
            }

            incomeChip.setOnCheckedChangeListener { _, isChecked ->
                incomeFilter = isChecked
                updateChipAppearance(incomeChip, isChecked)
                showViewWithAnimation(incomeChip, 500)
                TransitionManager.beginDelayedTransition(filterBottomSheetBinding.root)
            }

            expensesChip.setOnCheckedChangeListener { _, isChecked ->
                expenseFilter = isChecked
                updateChipAppearance(expensesChip, isChecked)
                showViewWithAnimation(expensesChip, 500)
                TransitionManager.beginDelayedTransition(filterBottomSheetBinding.root)
            }

            sumChip.setOnCheckedChangeListener { _, isChecked ->
                paymentSumFromEditText.text?.clear()
                paymentSumToEditText.text?.clear()
                paymentSumFromLayout.isErrorEnabled = false
                paymentSumToLayout.isErrorEnabled = false
                paymentSumFilter = isChecked
                updateChipAppearance(sumChip, isChecked)
                paymentSumLinear.isVisible = isChecked
                showViewWithAnimation(sumChip, 500)
                TransitionManager.beginDelayedTransition(filterBottomSheetBinding.root)
            }

            paymentSumFromEditText.filters = arrayOf(DecimalDigitsInputFilter(2))
            paymentSumToEditText.filters = arrayOf(DecimalDigitsInputFilter(2))

            applyButton.setOnClickListener {
                if (filterDate) {
                    isFiltered = true
                    binding.resetButton.isVisible = true
                    getHistory(true)
                }
                setAdapter()
                filterBottomSheet.dismiss()
            }

            if (!displayDateFrom.isNullOrEmpty() && !displayDateTo.isNullOrEmpty()) {
                periodEditText.text = MainManagerService().toEditable("от $displayDateFrom до $displayDateTo")
            }

            incomeChip.isChecked = incomeFilter
            expensesChip.isChecked = expenseFilter
            sumChip.isChecked = paymentSumFilter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() = with(filterBottomSheetBinding) {
        paymentSumFrom = paymentSumFromEditText.text.toString().toDoubleOrNull()
        paymentSumTo = paymentSumToEditText.text.toString().toDoubleOrNull()
        if (paymentSumFilter && (paymentSumFrom == null || paymentSumTo == null)) {
            paymentSumFromLayout.error = "Обязательное поле"
            paymentSumToLayout.error = "Обязательное поле"
            return
        }

        if (incomeFilter && expenseFilter) {
            filteredList = historyList
            binding.resetButton.isVisible = true
            isFiltered = true
        }

        if (incomeFilter && !expenseFilter) {
            filteredList = historyList.filter { it.TotalAmount ?: 0.0 > 0.0 } as ArrayList<Operation>
            binding.resetButton.isVisible = true
            isFiltered = true
        }

        if (expenseFilter && !incomeFilter) {
            filteredList = historyList.filter { it.TotalAmount ?: 0.0 < 0.0 } as ArrayList<Operation>
            binding.resetButton.isVisible = true
            isFiltered = true
        }

        if (paymentSumFilter) {
            filteredList = when {
                incomeFilter && !expenseFilter -> {filteredList.filter {
                    (it.TotalAmount ?: 0.0) > 0.0 && it.TotalAmount!! >= paymentSumFrom!! && it.TotalAmount <= paymentSumTo!!
                } as ArrayList<Operation>}

                !incomeFilter && expenseFilter -> {filteredList.filter {
                    (it.TotalAmount ?: 0.0) < 0.0 && abs(it.TotalAmount!!) >= paymentSumFrom!! && abs(it.TotalAmount!!) <= paymentSumTo!!
                } as ArrayList<Operation>}

                incomeFilter && expenseFilter -> {filteredList.filter {
                    abs(it.TotalAmount ?: 0.0) >= paymentSumFrom!! && abs(it.TotalAmount!!) <= paymentSumTo!!
                } as ArrayList<Operation>}

                else -> {filteredList.filter {
                    abs(it.TotalAmount ?: 0.0) >= paymentSumFrom!! && abs(it.TotalAmount!!) <= paymentSumTo!!
                } as ArrayList<Operation>}
            }
            binding.resetButton.isVisible = true
            isFiltered = true
        }

        if (!paymentSumFilter && !incomeFilter && !expenseFilter) {
            filteredList = historyList
        }

        adapter = OperationsHistoryAdapter(filteredList, this@PaymentHistoryActivity)
        binding.historyRecycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun updateChipAppearance(chip: Chip, isChecked: Boolean) {
        if (isChecked) {
            chip.setTextColor(ContextCompat.getColor(this, R.color.white))
            chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.green)
            chip.checkedIconTint = ContextCompat.getColorStateList(this, R.color.white)
        } else {
            chip.setTextColor(ContextCompat.getColor(this, R.color.green))
            chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.lightToDark)
        }
    }
}
