package tj.paykar.shop.domain.adapter.wallet

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.decode.SvgDecoder
import coil.load
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.PaymentCategory
import tj.paykar.shop.databinding.WalletItemPaymentsListBinding
import tj.paykar.shop.presentation.wallet.payment.SubPaymentsListActivity


class PaymentListAdapter(private val list: List<PaymentCategory>,
                         private val context: Context,
                         private val accountNumber: String): RecyclerView.Adapter<PaymentListAdapter.ViewHolder>() {

    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemPaymentsListBinding.bind(item)
        fun bind(category: PaymentCategory) = with(binding) {
            paymentTitle.text = category.CategoryName
            image.load(category.Icon) {
                decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
            }
        }

        init {
            binding.itemView.setOnClickListener {
                val intent = Intent(context, SubPaymentsListActivity::class.java)
                intent.putExtra("categoryId", list[adapterPosition].CategoryId)
                intent.putExtra("categoryName", list[adapterPosition].CategoryName)
                intent.putExtra("hasCategory", list[adapterPosition].HasCategory)
                intent.putExtra("selectPaymentAccount", accountNumber)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_payments_list, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}