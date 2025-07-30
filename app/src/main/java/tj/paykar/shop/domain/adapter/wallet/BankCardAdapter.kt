package tj.paykar.shop.domain.adapter.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.BankCard
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletBottomSheetBankCardInfoBinding
import tj.paykar.shop.databinding.WalletItemLinkedBankCardBinding
import tj.paykar.shop.domain.usecase.wallet.BankCardManagerService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentHistoryActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentsListActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.data.TRANSFER_TO_BANK_CARDS_PAYMENT_ID
import tj.paykar.shop.data.TRANSFER_TO_VISA_CARDS_PAYMENT_ID
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.databinding.WalletBottomSheetEditCardNameBinding
import tj.paykar.shop.databinding.WalletBottomSheetResetCardPinCodeBinding
import tj.paykar.shop.presentation.wallet.HomeActivity

class BankCardAdapter(private var list: List<BankCard>, private val context: Context): RecyclerView.Adapter<BankCardAdapter.ViewHolder>() {

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemLinkedBankCardBinding.bind(item)
        @SuppressLint("ResourceAsColor")
        fun bind(card: BankCard) = with(binding) {

            if (list[adapterPosition].Type == 1 && list[adapterPosition].IsLocalCard == true) {
                itemViewConstraint.setBackgroundResource(R.drawable.paykar_korti_milli_background_vertical)
                numberTitle.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.white)))
                balance.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.white)))
                balanceTitle.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.white)))
            }
            else if (list[adapterPosition].Type == 1 && list[adapterPosition].IsLocalCard == false) {
                itemViewConstraint.setBackgroundResource(R.drawable.korti_milli_background_vertical)
                numberTitle.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.black)))
                balance.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.black)))
                balanceTitle.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.black)))
            }
            else if (list[adapterPosition].Type == 3) {
                itemViewConstraint.setBackgroundResource(R.drawable.paykar_visa_background_vertical)
                numberTitle.setTextColor(context.resources.getColor(R.color.white))
                balance.setTextColor(context.resources.getColor(R.color.white))
                balanceTitle.setTextColor(context.resources.getColor(R.color.white))
            }

            numberTitle.text = card.Number
            if (card.Account.ShowBalance == true) {
                val balance = card.Account.Balance.toString().replace(".", ",")
                balanceTitle.text = balance
            } else {
                balanceTitle.text = "****"
            }
        }

        init {
            binding.itemView.setOnClickListener {
                addBlinkEffect(it)
                val bottomSheetDialog = BottomSheetDialog(context)
                val bottomSheetBinding = WalletBottomSheetBankCardInfoBinding.inflate(LayoutInflater.from(context))
                val bottomSheetView = bottomSheetBinding.root
                bottomSheetDialog.setContentView(bottomSheetView)

                bottomSheetDialog.setOnShowListener {
                    val dialog = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    val layoutParams = dialog!!.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    dialog.layoutParams = layoutParams
                    val behavior = BottomSheetBehavior.from(dialog)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

                bottomSheetDialog.show()

                bottomSheetBinding.apply {
                    if (list[adapterPosition].Type == 1 && list[adapterPosition].IsLocalCard == true) {
                        cardBackground.setBackgroundResource(R.drawable.paykar_korti_milli_background)
                        cardNumberTitle.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.white)))
                        //cardNumberTitle.text = list[adapterPosition].Number
                    }
                    else if (list[adapterPosition].Type == 1 && list[adapterPosition].IsLocalCard == false) {
                        cardBackground.setBackgroundResource(R.drawable.korti_milli_background)
                        cardNumberTitle.setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.black)))
                       // cardNumberTitle.text = list[adapterPosition].Number
                    }
                    else if (list[adapterPosition].Type == 3) {
                        resetPinCodeBtn.isVisible = true
                        blockBtn.isVisible = true
                        cardBackground.setBackgroundResource(R.drawable.paykar_visa_background)
                        cardNumberTitle.setTextColor(context.resources.getColor(R.color.white))
                    }

                    cardNumberTitle.text = list[adapterPosition].Number?.chunked(4)?.joinToString(" ")
                    val accountNumber = list[adapterPosition].Account.Account
                    //expirationDateTitle.text = formatCardExpiryDate(list[adapterPosition].ExpDate ?: "")
                    if (list[adapterPosition].Account.ShowBalance == true) {
                        val balance = list[adapterPosition].Account.Balance.toString().replace(".", ",")
                        balanceTitle.text = "$balance с"
                    } else {
                        balanceCard.isGone = true
                    }
                    //cardOwnerNameTitle.text = list[adapterPosition].Name
                    cardNameTitle.text = list[adapterPosition].Name?.takeIf { it.isNotEmpty() } ?: "Информация о карте"

                    cancelBtn.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }

                    replenishBtn.setOnClickListener {
                        if (list[adapterPosition].Type == 1) {
                            val intent = Intent(context, PaymentActivity::class.java)
                            intent.putExtra("clearAccount", accountNumber)
                            intent.putExtra("serviceId", TRANSFER_TO_BANK_CARDS_PAYMENT_ID)
                            intent.putExtra("serviceName", "Пополнить карту")
                            context.startActivity(intent)
                        }
                        else if (list[adapterPosition].Type == 3) {
                            val intent = Intent(context, PaymentActivity::class.java)
                            intent.putExtra("clearAccount", accountNumber)
                            intent.putExtra("serviceId", TRANSFER_TO_VISA_CARDS_PAYMENT_ID)
                            intent.putExtra("activityType", "savedService")
                            intent.putExtra("savedServiceAccount", list[adapterPosition].Number)
                            intent.putExtra("savedServiceAccount2", "")
                            context.startActivity(intent)
                        }
                    }

                    payBtn.setOnClickListener {
                        val intent = Intent(context, PaymentsListActivity::class.java)
                        intent.putExtra("selectPaymentAccount", accountNumber)
                        context.startActivity(intent)
                    }

                    paymentHistoryBtn.setOnClickListener {
                        val intent = Intent(context, PaymentHistoryActivity::class.java)
                        intent.putExtra("accountNumber", accountNumber)
                        context.startActivity(intent)
                    }

                    editCardNameBtn.setOnClickListener {
                        showEditCardBottomSheet(list[adapterPosition], bottomSheetDialog)
                    }

                    resetPinCodeBtn.setOnClickListener {
                        showPinCodeBottomSheet(list[adapterPosition])
                    }

                    blockBtn.setOnClickListener {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Заблокировать карту")
                            .setMessage("Вы действительно хотите заблокировать карту?")
                            .setPositiveButton("Да") {_, _ -> blockCard(list[adapterPosition].Id ?: 0)}
                            .setNegativeButton("Отмена") {_, _ -> }
                            .show()
                    }

                    deleteBankCardBtn.setOnClickListener {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Удалить карту")
                            .setMessage("Вы действительно хотите отвязать карту?")
                            .setPositiveButton("Да"){_, _ ->
                                deleteBankCard(list[adapterPosition].Id ?: 0, bottomSheetDialog)
                            }
                            .setNegativeButton("Отмена") {_, _ ->}
                            .show()
                    }
                }
            }
        }
    }

    fun formatCardExpiryDate(expiryDate: String, delimiter: String = "/"): String {
        if (expiryDate.length >= 2 && expiryDate.length % 2 == 0) {
            val midpoint = expiryDate.length / 2
            return expiryDate.substring(0, midpoint) + delimiter + expiryDate.substring(midpoint)
        }
        return expiryDate
    }

    private fun showEditCardBottomSheet(card: BankCard, cardInfoBottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = BottomSheetDialog(context)
        val binding = WalletBottomSheetEditCardNameBinding.inflate(LayoutInflater.from(context))
        bottomSheet.setContentView(binding.root)

        bottomSheet.setOnShowListener {
            val dialog = bottomSheet.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val layoutParams = dialog!!.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.layoutParams = layoutParams
            val behavior = BottomSheetBehavior.from(dialog)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheet.show()

        binding.apply {
            cancelBtn.setOnClickListener {
                bottomSheet.dismiss()
            }

            cardNameEditText.text = MainManagerService().toEditable(card.Name.toString())

            cardNameEditText.doOnTextChanged { _, _, _, _ ->
                cardNameLayout.isErrorEnabled = false
            }

            saveButton.setOnClickListener {
                if (cardNameEditText.text.isNullOrEmpty()) {
                    cardNameLayout.error = "Обязательное поле"
                } else {
                    editCardName(cardNameEditText.text.toString(), card.Id ?: 0, bottomSheet, cardInfoBottomSheetDialog)
                }
            }
        }
    }

    private fun showPinCodeBottomSheet(card: BankCard) {
        val bottomSheet = BottomSheetDialog(context)
        val bottomSheetBinding = WalletBottomSheetResetCardPinCodeBinding.inflate(LayoutInflater.from(context))
        bottomSheet.setContentView(bottomSheetBinding.root)

        bottomSheet.show()

        bottomSheetBinding.apply {
            cancelBtn.setOnClickListener {
                bottomSheet.dismiss()
            }

            pinCodeEditText.doOnTextChanged { _, _, _, _ ->
                pinCodeLayout.isErrorEnabled = false
            }

            saveButton.setOnClickListener {
                if (pinCodeEditText.text.isNullOrEmpty()) {
                    pinCodeLayout.error = "Обязательное поле"
                } else {
                    resetPinCode(card.Id ?: 0, pinCodeEditText.text.toString(), bottomSheet)
                }
            }
        }
    }

    private fun resetPinCode(cardId: Int, pinCode: String, bottomSheet: BottomSheetDialog) {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val progressDialog = CustomProgressDialog(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        } else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().resetPinCode(customerId, cardId, pinCode, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            bottomSheet.dismiss()
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (_: Exception) {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Произошла ошибка")
                        .setMessage("Попробуйте по позже!")
                        .setPositiveButton("Понятно") {_, _ -> }
                        .show()
                }
            }
        }
    }

    private fun editCardName(name: String, cardId: Int, bottomSheet: BottomSheetDialog, cardInfoBottomSheetDialog: BottomSheetDialog) {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val progressDialog = CustomProgressDialog(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        } else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().editCardName(customerId, cardId, name, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            bottomSheet.dismiss()
                            cardInfoBottomSheetDialog.dismiss()
                            getBankCardList()
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (_: Exception) {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Произошла ошибка")
                        .setMessage("Попробуйте по позже!")
                        .setPositiveButton("Понятно") {_, _ -> }
                        .show()
                }
            }
        }
    }

    private fun blockCard(cardId: Int) {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val progressDialog = CustomProgressDialog(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        } else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().blockCard(customerId, cardId, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Блокировка карты")
                                .setMessage("Операция завершена успешно. Карта заблокирована и больше не доступна для использования.")
                                .setPositiveButton("Понятно") {_, _ ->}
                                .show()
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (_: Exception) {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Произошла ошибка")
                        .setMessage("Попробуйте ещё раз!")
                        .setPositiveButton("Попробовать ещё раз") {_, _ -> blockCard(cardId) }
                        .show()
                }
            }
        }
    }

    private fun deleteBankCard(cardId: Int, bottomSheet: BottomSheetDialog) {
        val ipAddress = IpAddressStorage(context).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val progressDialog = CustomProgressDialog(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().deleteBankCard(customerId, cardId, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            bottomSheet.dismiss()
                            val activity = context as HomeActivity
                            activity.getBankCardList()
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("OK") {dialog, _ -> dialog.cancel()}
                            .show()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBankCardList() {
        val ipAddress = DeviceInfo().getIPAddress(true) ?: ""
        val imei = DeviceInfo().getDeviceIMEI(context)
        val progressDialog = CustomProgressDialog(context)
        val customerId = UserStorage(context).customerId ?: 0
        val token = UserStorage(context).token
        val isOnline = MainManagerService().internetConnection(context)
        if (!isOnline) {
            MainManagerService().noInternetAlert(context)
        }
        else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceInfo = RequestDeviceInfoModel("1.0", token, imei)
                    val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                    val request = BankCardManagerService().bankCardList(customerId, deviceInfo, requestInfo)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Log.d("--RequestInfo", "response: ${request.body()}")
                        val response = request.body()
                        if (response?.ResultCode == 0) {
                            list = response.Cards ?: emptyList()
                            notifyDataSetChanged()
                        } else {
                            requestResultCodeAlert(response?.ResultCode ?: 0, context, response?.ResultDesc ?: "")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Произошла ошибка")
                            .setMessage("Попробуйте ещё раз!")
                            .setPositiveButton("OK") {dialog, _ -> dialog.cancel()}
                            .show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_linked_bank_card, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == list.size - 1) {
            layoutParams.marginEnd = (16 * context.resources.displayMetrics.density).toInt()
        } else {
            layoutParams.marginEnd = 0
        }
        holder.itemView.layoutParams = layoutParams
    }
}