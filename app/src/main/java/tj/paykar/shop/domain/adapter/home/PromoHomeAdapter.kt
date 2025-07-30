package tj.paykar.shop.domain.adapter.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.PromoModel
import tj.paykar.shop.databinding.ItemHomePromoBinding
import tj.paykar.shop.presentation.shop.promo.PromoDetailActivity

class PromoHomeAdapter(context: Context): RecyclerView.Adapter<PromoHomeAdapter.PromoHomeHolder>() {

    var promoList = ArrayList<PromoModel>()
    private val mContext = context

    @SuppressLint("ClickableViewAccessibility")
    class PromoHomeHolder(item: View, promoList: ArrayList<PromoModel>) : RecyclerView.ViewHolder(item)  {
        val binding = ItemHomePromoBinding.bind(item)
        fun bind(promo: PromoModel, context: Context) = with(binding) {
            Glide.with(context)
                .load("https://paykar.shop" + promo.detail_picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(promoImage)
            promoTitle.text = ""
        }

        init {
            binding.cardView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }
                }
                false
            }
            binding.cardView.setOnClickListener { itemView: View ->
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoHomeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_home_promo, parent, false)
        return PromoHomeHolder(view, promoList)
    }

    override fun onBindViewHolder(holder: PromoHomeHolder, position: Int) {
        holder.bind(promoList[position], mContext)
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

//        if (promoList.size == 1) {
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        } else {
//            layoutParams.width = (350 * mContext.resources.displayMetrics.density).toInt()
//            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        }

        if (position == promoList.size - 1) {
            layoutParams.marginEnd = (16 * mContext.resources.displayMetrics.density).toInt()
        } else {
            layoutParams.marginEnd = 0
        }

        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return promoList.size
    }
}