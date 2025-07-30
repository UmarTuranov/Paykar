package tj.paykar.shop.domain.adapter.notifycation

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.NotifycationModel
import tj.paykar.shop.databinding.ItemNotifyBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.webview.WebViewActivity


class NotifycationAdapter (context: Context): RecyclerView.Adapter<NotifycationAdapter.RowHolder>() {

    var notifyList = ArrayList<NotifycationModel>()
    private val mContext = context

    class RowHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemNotifyBinding.bind(item)

        fun bind(notify: NotifycationModel, context: Context) = with(binding) {
            Log.d("---D Notify", notify.toString())

            title.text = notify.title
            message.text = notify.description
            link.setOnClickListener {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("webTitle", notify.title)
                intent.putExtra("webUrl", notify.link)
                context.startActivity(intent)
            }

            Glide.with(context)
                .load("https://paykar.shop" + notify.picture)
                .placeholder(R.drawable.notify)
                .centerCrop()
                .transform(RoundedCorners(20))
                .into(image)

            val dateNotify = tj.paykar.shop.domain.usecase.wallet.MainManagerService().convertDateFormat(notify.createDate, "yyyy-MM-dd HH:mm:ss", "dd.MM.yyyy | HH:mm")
            date.text = dateNotify
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_notify, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        return holder.bind(notifyList[position], mContext)
    }

    override fun getItemCount(): Int {
        return notifyList.size
    }
}