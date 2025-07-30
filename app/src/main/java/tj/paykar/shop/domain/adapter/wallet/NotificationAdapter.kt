package tj.paykar.shop.domain.adapter.wallet

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.NotificationModel
import tj.paykar.shop.databinding.WalletItemNotificationBinding
import tj.paykar.shop.domain.usecase.wallet.MainManagerService

class NotificationAdapter(private val list: List<NotificationModel>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = WalletItemNotificationBinding.bind(item)
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(notification: NotificationModel) = with(binding) {
            headerTitle.text = notification.Name;
            descriptionTitle.text = notification.Description
            createDateTitle.text = MainManagerService().convertDateTime(notification.NotificationDate ?: "")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item_notification, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(list[position])
    }
}