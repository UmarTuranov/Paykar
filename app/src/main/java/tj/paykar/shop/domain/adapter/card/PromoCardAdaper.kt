package tj.paykar.shop.domain.adapter.card

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
import tj.paykar.shop.data.model.loyalty.PromoCardModel
import tj.paykar.shop.databinding.ItemPromoBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.presentation.card.promo.PromoDetailCardActivity

class PromoCardAdaper (context: Context): RecyclerView.Adapter<PromoCardAdaper.PromoHolder>() {

    var promoList = ArrayList<PromoCardModel>()
    var mContext = context

    class PromoHolder(view: View, promoList: ArrayList<PromoCardModel>): RecyclerView.ViewHolder(view) {

        val binding = ItemPromoBinding.bind(view)

        fun bind(promo: PromoCardModel, context: Context) = with(binding) {

            Glide.with(context)
                .load("https://paykar.tj" + promo.detail_picture)
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

            view.setOnClickListener { itemView: View ->
                val position: Int = adapterPosition
                val intent = Intent(view.context, PromoDetailCardActivity::class.java)
                intent.putExtra("title", promoList[position].name)
                intent.putExtra("period", promoList[position].period)
                intent.putExtra("description", promoList[position].detail)
                intent.putExtra("image", promoList[position].detail_picture)
                view.context.startActivity(intent)
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
        val sendReq = CardManagerService().promoList()
        val response: ArrayList<PromoCardModel> = sendReq
        this.promoList = response
    }

}