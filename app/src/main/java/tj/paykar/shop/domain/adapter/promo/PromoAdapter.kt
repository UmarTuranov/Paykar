package tj.paykar.shop.domain.adapter.promo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.PromoModel
import tj.paykar.shop.databinding.ItemPromoBinding
import tj.paykar.shop.domain.usecase.shop.PromoManagerService
import tj.paykar.shop.presentation.shop.promo.PromoDetailActivity

class PromoAdapter(context: Context): RecyclerView.Adapter<PromoAdapter.PromoHolder>() {

    private var promoList = ArrayList<PromoModel>()
    private val mContext = context

    class PromoHolder(item: View, promoList: ArrayList<PromoModel>) : RecyclerView.ViewHolder(item)  {
        val binding = ItemPromoBinding.bind(item)
        fun bind(promo: PromoModel, context: Context) = with(binding) {

            Glide.with(context)
                .load("https://paykar.shop" + promo.detail_picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(promoImage)

            if (promo.period == "" || promo.period == null) {
                promoTitle.isGone = true
            } else {
                promoTitle.isVisible = true
                promoTitle.text = promo.period
            }
        }

        init {

            item.setOnClickListener { itemView: View ->
                val position: Int = adapterPosition
                val intent = Intent(item.context, PromoDetailActivity::class.java)
                intent.putExtra("title", promoList[position].name)
                intent.putExtra("period", promoList[position].period)
                intent.putExtra("description", promoList[position].detail)
                intent.putExtra("image", promoList[position].detail_picture)
                item.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_promo, parent, false)
        return PromoHolder(view, promoList)
    }

    override fun onBindViewHolder(holder: PromoHolder, position: Int) {
        return holder.bind(promoList[position], mContext)
    }

    override fun getItemCount(): Int {
        return promoList.size
    }

    suspend fun sendRequest() {
        val sendReq = PromoManagerService().promoList()
        val response: ArrayList<PromoModel> = sendReq
        response.reverse()
        this.promoList = response
    }

}