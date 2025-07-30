package tj.paykar.shop.domain.adapter.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.databinding.WalletItemSavedServicesBinding
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity

class SavedServicesAdapter(private var list: ArrayList<HashMap<String, Any>>, private val context: Context): RecyclerView.Adapter<SavedServicesAdapter.ViewHolder>() {
    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemSavedServicesBinding.bind(item)
        @SuppressLint("NotifyDataSetChanged")
        fun bind(service: HashMap<String, Any>) = with(binding) {
            val serviceName = service["serviceName"] as? String?
            val serviceId = service["serviceId"] as? Int?
            val serviceAccount = service["serviceAccount"] as? String?
            val serviceAccount2 = service["serviceAccount2"] as? String?
            val paymentSum = service["paymentSum"] as? Double?
            val icon = service["icon"] as? String?

            Glide.with(context)
                .load(icon)
                .into(image)

            paymentTitle.text = serviceName
            accountTitle.text = serviceAccount

            itemView.setOnClickListener {
                val intent = Intent(context, PaymentActivity::class.java)
                intent.putExtra("serviceId", serviceId)
                intent.putExtra("savedServiceAccount", serviceAccount)
                intent.putExtra("savedServiceAccount2", serviceAccount2)
                intent.putExtra("savedServicePaymentSum", paymentSum)
                intent.putExtra("activityType", "savedService")
                context.startActivity(intent)
            }

            itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Удалить избранный сервис")
                    .setMessage("Вы действительно хотите удалить этот избранный сервис?")
                    .setPositiveButton("Да") {_, _ ->
                        SavedServicesStorage(context).deleteService(serviceId ?: 0, serviceAccount ?: "")
                        this@SavedServicesAdapter.list = SavedServicesStorage(context).getSavedServices()
                        notifyDataSetChanged()
                    }
                    .setNegativeButton("Отмена") {_, _ ->}
                    .show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_saved_services, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(list[position])
    }
}