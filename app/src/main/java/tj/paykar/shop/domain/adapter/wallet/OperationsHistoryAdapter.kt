package tj.paykar.shop.domain.adapter.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import tj.paykar.shop.R
import tj.paykar.shop.data.QR_PAYMENT_ID
import tj.paykar.shop.data.model.wallet.Operation
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.databinding.WalletBottomSheetAddSavedServiceBinding
import tj.paykar.shop.databinding.WalletBottomSheetPaymentHistoryBinding
import tj.paykar.shop.databinding.WalletBottomSheetShareReceiptBinding
import tj.paykar.shop.databinding.WalletItemPaymentHistoryBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

@Suppress("DEPRECATION")
class OperationsHistoryAdapter(private var list: List<Operation>, private val context: Context): RecyclerView.Adapter<OperationsHistoryAdapter.ViewHolder>() {

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val progressDialog = CustomProgressDialog(context)
        private val binding = WalletItemPaymentHistoryBinding.bind(item)
        private val yellow = ColorStateList(arrayOf(IntArray(0)), intArrayOf(context.resources.getColor(R.color.orange)))
        private val red = ColorStateList(arrayOf(IntArray(0)), intArrayOf(context.resources.getColor(R.color.red)))
        private val green = ColorStateList(arrayOf(IntArray(0)), intArrayOf(context.resources.getColor(R.color.paykar)))
        private val blackToWhite = ColorStateList(arrayOf(IntArray(0)), intArrayOf(context.resources.getColor(R.color.blackToWhite)))

        fun bind(operation: Operation) = with(binding) {
            paymentTitle.text = operation.ServiceName
            operationTypeTitle.text = operation.OperationType
            when (operation.State) {
                "Выполняется" -> {
                    paymentDateTitle.text = operation.State
                    paymentDateTitle.setTextColor(yellow)
                }
                "Ошибка" -> {
                    paymentDateTitle.text = operation.State
                    paymentDateTitle.setTextColor(red)
                }
                "Отменён" -> {
                    paymentDateTitle.text = operation.State
                    paymentDateTitle.setTextColor(red)
                }
                "Успешный" -> {
                    Log.d("--D OperationDate", operation.OperationDate.toString())
                    paymentDateTitle.text = MainManagerService().dateConvert(operation.OperationDate ?: "")
                    paymentDateTitle.setTextColor(blackToWhite)
                }
            }
            val totalAmountString = operation.TotalAmount.toString().replace(".", ",")
            amount.text = "$totalAmountString с"
            Glide.with(context)
                .load(operation.Icon)
                .into(image)

            binding.itemView.setOnClickListener {
                val historyBottomSheet = BottomSheetDialog(context)
                val historyInfoBinding = WalletBottomSheetPaymentHistoryBinding.inflate(LayoutInflater.from(context))
                historyBottomSheet.setContentView(historyInfoBinding.root)
                historyBottomSheet.show()
                val dialog = historyBottomSheet.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                val layoutParams = dialog!!.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog.layoutParams = layoutParams
                val behavior = BottomSheetBehavior.from(dialog)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                val serviceName = operation.ServiceName ?: ""
                val id = operation.Id ?: 0
                val serviceId = operation.ServiceId ?: 0
                val commentary = operation.Comment
                val commission = operation.Comission ?: 0.0
                val serviceCategory = operation.ServiceCategoryName
                val createDate = MainManagerService().dateConvert(operation.OperationDate ?: "")
                val paymentMethod = operation.PaymentMethod
                val sender = operation.Sender
                val recipient = operation.Recipient ?: ""
                val state = operation.State
                val icon = operation.Icon
                val amount = operation.Amount ?: 0.0
                val totalAmount = operation.TotalAmount ?: 0.0

                historyInfoBinding.apply {
                    transactionIdTitle.text = id.toString()
                    operationTypeTitle.text = operation.OperationType
                    commissionTitle.text = "${commission.toString().replace(".", ",")} с"
                    commentaryTitle.text = commentary
                    serviceNameTitle.text = serviceName
                    serviceCategoryTitle.text = serviceCategory
                    operationDate.text = createDate
                    paymentTitle.text = paymentMethod
                    senderDataTitle.text = sender
                    receiverTitle.text = recipient
                    statusTitle.text = state
                    Glide.with(context)
                        .load(operation.Icon)
                        .into(serviceIcon)
                    totalAmountTitle.text = "$totalAmountString с"

                    when (state) {
                        "Выполняется" -> {
                            statusTitle.setTextColor(yellow)
                            statusIcon.isGone = true
                            statusAnimation.isVisible = true
                        }
                        "Ошибка" -> {
                            statusTitle.setTextColor(red)
                            statusIcon.setImageResource(R.drawable.ic_error)
                        }
                        "Отменён" -> {
                            statusTitle.setTextColor(red)
                            statusIcon.setImageResource(R.drawable.ic_error)
                        }
                        "Успешный" -> {
                            statusTitle.setTextColor(green)
                            statusIcon.setImageResource(R.drawable.ic_successful)
                        }
                    }

                    if (commentary.isNullOrEmpty()) {
                        commentaryLinear.isGone = true
                    }

                    if (totalAmount < 0.0 && serviceId != QR_PAYMENT_ID && state == "Успешный") {
                        saveServiceLayout.isVisible = true
                        repeatPaymentLayout.isVisible = true
                        shareBtn.isVisible = true
                    }

                    val existingSavedService = SavedServicesStorage(context).existingService(serviceId, recipient)

                    if (existingSavedService) {
                        historyInfoBinding.saveServiceIcon.setImageResource(R.drawable.ic_save_filled)
                        historyInfoBinding.saveServiceTitle.text = "Удалить из избранных"
                    }

                    repeatPaymentLayout.setOnClickListener {
                        addBlinkEffect(it)
                        val intent = Intent(context, PaymentActivity::class.java)
                        intent.putExtra("serviceId", operation.ServiceId)
                        intent.putExtra("savedServiceAccount", recipient)
                        intent.putExtra("savedServiceAccount2", "")
                        intent.putExtra("savedServicePaymentSum", abs(operation.Amount ?: 0.0))
                        intent.putExtra("activityType", "savedService")
                        context.startActivity(intent)
                    }

                    saveServiceLayout.setOnClickListener {
                        addBlinkEffect(it)
                        saveService(serviceId, recipient, amount, icon, historyInfoBinding)
                    }

                    shareBtn.setOnClickListener {
                        shareCheque(id,
                            createDate,
                            serviceName,
                            amount,
                            commission,
                            totalAmount,
                            recipient,
                            paymentMethod ?: "")

                    }
                }
            }
        }
    }

    private fun shareCheque(
        transactionId: Int,
        createDate: String,
        serviceName: String,
        amount: Double,
        commission: Double,
        totalAmount: Double,
        recipient: String,
        paymentMethod: String
    ) {
        val chequeBottomSheet = BottomSheetDialog(context)
        val chequeBinding = WalletBottomSheetShareReceiptBinding.inflate(LayoutInflater.from(context))
        chequeBottomSheet.setContentView(chequeBinding.root)
        val dialog = chequeBottomSheet.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = dialog!!.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.layoutParams = layoutParams
        val behavior = BottomSheetBehavior.from(dialog)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        chequeBottomSheet.show()
        chequeBinding.apply {
            transactionNumber.text = transactionId.toString()
            dateAndTime.text = createDate
            service.text = serviceName
            paymentSum.text = amount.toString()
            chequeBinding.commission.text = commission.toString()
            totalSum.text = totalAmount.toString()
            transferAccount.text = recipient
            chequeBinding.paymentMethod.text = paymentMethod

            chequeBinding.shareBtn.setOnClickListener {
                val content = chequeCardRoot
                val pdfFile = createPdf(content, "ID транзакция $transactionId")
                sharePdf(pdfFile)
            }

            backBtn.setOnClickListener {
                addBlinkEffect(it)
                chequeBottomSheet.dismiss()
            }
        }
    }

    private fun saveService(serviceId: Int, recipient: String?, amount: Double?, icon: String?, binding: WalletBottomSheetPaymentHistoryBinding) = with(binding) {
        val existingService = SavedServicesStorage(context).existingService(serviceId, recipient ?: "")
        if (existingService) {
            SavedServicesStorage(context).deleteService(serviceId, recipient ?: "")
            saveServiceIcon.setImageResource(R.drawable.ic_save)
            saveServiceTitle.text = "Добавить в избранную"
            showViewWithAnimation(saveServiceLayout)
        } else {
            val saveServiceSheet = BottomSheetDialog(context)
            val saveServiceSheetBinding =
                WalletBottomSheetAddSavedServiceBinding.inflate(LayoutInflater.from(context))
            val saveServiceView = saveServiceSheetBinding.root
            saveServiceSheet.setContentView(saveServiceView)
            saveServiceSheet.show()

            saveServiceSheetBinding.apply {
                saveButton.setOnClickListener {
                    if (nameEditText.text.isNullOrEmpty()) {
                        nameLayout.error = "Обязательное поле"
                    } else {
                        nameLayout.isErrorEnabled = false
                        SavedServicesStorage(context).savedService(
                            nameEditText.text.toString(),
                            serviceId,
                            recipient ?: "",
                            "",
                            abs(amount ?: 0.0),
                            icon ?: ""
                        )
                        saveServiceSheet.dismiss()
                        saveServiceIcon.setImageResource(R.drawable.ic_save_filled)
                        saveServiceTitle.text = "Удалить из избранных"
                        showViewWithAnimation(saveServiceLayout)
                    }
                }
            }
        }
    }

    private fun createPdf(view: View, fileName: String): File {
        val pdfFile = File(context.cacheDir, "$fileName.pdf")
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        view.draw(canvas)
        document.finishPage(page)
        document.writeTo(FileOutputStream(pdfFile))
        document.close()
        return pdfFile
    }

    private fun sharePdf(pdfFile: File) {
        val uri = FileProvider.getUriForFile(context, "tj.paykar.shop.provider", pdfFile)
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться с помощью:"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_payment_history, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilterList(newList: List<Operation>) {
        this.list = newList
        notifyDataSetChanged()
    }
}