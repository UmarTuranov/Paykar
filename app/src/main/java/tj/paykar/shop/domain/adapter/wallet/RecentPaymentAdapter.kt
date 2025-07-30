package tj.paykar.shop.domain.adapter.wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.databinding.WalletItemRecentPaymentBinding
import tj.paykar.shop.domain.reprository.wallet.RecentPaymentManager


class RecentPaymentAdapter(private val list: ArrayList<String>,
                         private val context: Context,
                         private val recentPayment: RecentPaymentManager): RecyclerView.Adapter<RecentPaymentAdapter.ViewHolder>() {

    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemRecentPaymentBinding.bind(item)
        fun bind(account: String) {
            binding.title.text = account

            binding.itemView.setOnClickListener {
                recentPayment.onClick(account)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_recent_payment, parent, false)
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