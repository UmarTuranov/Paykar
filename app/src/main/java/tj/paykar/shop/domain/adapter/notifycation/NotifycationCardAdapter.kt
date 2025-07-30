package tj.paykar.shop.domain.adapter.notifycation

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.loyalty.NotifycationCardModel
import tj.paykar.shop.databinding.ItemNotifycationCardBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.NotifyManagerService
import tj.paykar.shop.presentation.webview.WebViewActivity

class NotifycationCardAdapter (context: Context): RecyclerView.Adapter<NotifycationCardAdapter.RowHolder>() {

    var notifyList = ArrayList<NotifycationCardModel>()
    private val mContext = context

    class RowHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemNotifycationCardBinding.bind(item)

        fun bind(notify: NotifycationCardModel, context: Context) = with(binding) {
            image.setImageResource(R.drawable.icon_logo_green)
            title.text = notify.Title
            message.text = notify.Message
            val dateNotify = MainManagerService().dateConvert(notify.SendDate)
            date.text = dateNotify
            more.setOnClickListener {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("webTitle", "Новости")
                intent.putExtra("webUrl", "https://paykar.tj/news/")
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_notifycation_card, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        return holder.bind(notifyList[position], mContext)
    }

    override fun getItemCount(): Int {
        return notifyList.size
    }

    suspend fun sendRequest(phone: String, token: String) {
        val sendReq = NotifyManagerService()
        val response: ArrayList<NotifycationCardModel> = sendReq.notifyCardList(phone, token)
        this.notifyList = response
    }

}