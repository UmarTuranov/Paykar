package tj.paykar.shop.domain.adapter.wallet

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.Services
import tj.paykar.shop.databinding.WalletItemServicesListBinding
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity

class ServiceListAdapter(private val list: List<Services>,
                         private val context: Context,
                         private val accountNumber: String): RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {

    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemServicesListBinding.bind(item)
        fun bind(service: Services) = with(binding) {
            paymentTitle.text = service.ServiceName
            Glide.with(context)
                .load(service.Icon)
                .into(image)
        }

        init {
            binding.itemView.setOnClickListener {
                val intent = Intent(context, PaymentActivity::class.java)
                intent.putExtra("serviceId", list[adapterPosition].ServiceId)
                intent.putExtra("serviceName", list[adapterPosition].ServiceName)
                intent.putExtra("icon", list[adapterPosition].Icon)
                intent.putExtra("selectPaymentAccount", accountNumber)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_services_list, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}