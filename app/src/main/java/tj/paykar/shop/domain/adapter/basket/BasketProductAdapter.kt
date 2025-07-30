package tj.paykar.shop.domain.adapter.basket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.ProductItems
import tj.paykar.shop.databinding.ItemProductImageBinding

class BasketProductAdapter(context: Context): RecyclerView.Adapter<BasketProductAdapter.BasketProductHolder>() {

    var basketProductList = ArrayList<ProductItems>()
    private var mContext = context
    private var userId: Int = 0

    class BasketProductHolder(view: View, basketList: ArrayList<ProductItems>): RecyclerView.ViewHolder(view) {
        val binding = ItemProductImageBinding.bind(view)

        fun bind(basketProductItem: ProductItems, context: Context, userId: Int) = with(binding){
            Glide.with(context)
                .load("https://paykar.shop" + basketProductItem.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(productImage)

            productImage.clipToOutline = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketProductHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product_image, parent, false)
        return BasketProductHolder(itemView, basketProductList)
    }

    override fun onBindViewHolder(holder: BasketProductHolder, position: Int) {
        return holder.bind(basketProductList[position], mContext, userId)
    }

    override fun getItemCount(): Int {
        return basketProductList.size
    }

}