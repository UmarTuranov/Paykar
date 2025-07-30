package tj.paykar.shop.domain.adapter.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.Account
import tj.paykar.shop.databinding.WalletItemPaymentBankCardBinding
import tj.paykar.shop.domain.reprository.wallet.SelectBankCardService

class PaymentBankCardAdapter(private var list: List<Account>,
                             private val context: Context,
                             private val accountNumber: String,
                             private val onSelectCard: SelectBankCardService
): RecyclerView.Adapter<PaymentBankCardAdapter.ViewHolder>() {
    private var selectedPosition: Int
    var selectedAccount: Account

    init {
        this.list = list.sortedBy { it.AccountName != "Paykar Wallet" }
        selectedPosition = list.indexOfFirst { it.Account == accountNumber }.takeIf { it >= 0 } ?: 0
        selectedAccount = list[selectedPosition]
    }

    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemPaymentBankCardBinding.bind(item)
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(card: Account) = with(binding) {
            bankCardTitle.text = card.AccountName
            if (card.ShowBalance == true) {
                val balance = card.Balance.toString().replace("null", "0.0").replace(".", ",")
                balanceTitle.text = "$balance с"
            } else {
                balanceTitle.isGone = true
            }
            val colors = intArrayOf(
                context.resources.getColor(R.color.white),
                context.resources.getColor(R.color.paykar),
                context.resources.getColor(R.color.lightToDark),
                context.resources.getColor(R.color.blackToWhite),
            )
            val white = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[0]))
            val green = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[1]))
            val lightToDark = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[2]))
            val blackToWhite = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[3]))

            if (card.Type == 1) {
                bankCardCard.setCardBackgroundColor(green)
                balanceTitle.setTextColor(white)
                bankCardTitle.setTextColor(white)
                cardImage.setImageResource(R.drawable.plogo_svg)
                cardImage.imageTintList = white
            } else if (card.Type == 2) {
                cardImage.setImageResource(R.drawable.logo_korti_milli)
                cardImage.imageTintList = null
                cardImage.setPadding(0, 0, 0, 0)
                bankCardCard.setCardBackgroundColor(lightToDark)
                bankCardTitle.setTextColor(blackToWhite)
                balanceTitle.setTextColor(blackToWhite)
            } else if (card.Type == 5) {
                cardImage.setImageResource(R.drawable.visa)
                cardImage.imageTintList = null
                cardImage.setPadding(0, 0, 0, 0)
                bankCardCard.setCardBackgroundColor(lightToDark)
                bankCardTitle.setTextColor(blackToWhite)
                balanceTitle.setTextColor(blackToWhite)
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                selectedAccount = list[selectedPosition]
                onSelectCard.onSelectCard(true, selectedAccount)
                Log.d("--P Position", adapterPosition.toString())
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_payment_bank_card, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}