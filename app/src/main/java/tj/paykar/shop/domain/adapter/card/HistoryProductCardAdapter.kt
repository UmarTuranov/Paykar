package tj.paykar.shop.domain.adapter.card

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.loyalty.DocumentDetails
import tj.paykar.shop.databinding.ItemCardHistoryDetailV2Binding

class HistoryProductCardAdapter(context: Context): RecyclerView.Adapter<HistoryProductCardAdapter.ProductHolder>() {

    var productList = ArrayList<DocumentDetails>()
    var mContext = context

    class ProductHolder(view: View): RecyclerView.ViewHolder(view) {
        val biding = ItemCardHistoryDetailV2Binding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(product: DocumentDetails) = with(biding) {
            productNameCard.text = product.ProductName
            productPriceCard.text = "${product.TotalPrice} сомони"
            //val quantity = product.Quantity.toInt()
            productMeasureCard.text = "${product.Quantity} шт"
            if (product.TotalPriceDiscounted != product.TotalPrice){
                productOffPriceCard.text = "${product.TotalPriceDiscounted} сомони"
            } else {
                productCardPriceTitle.isVisible = false
                productOffPriceCard.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.item_card_history_detail_v2, parent, false)
        return ProductHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        return holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProductList(productListArray: ArrayList<DocumentDetails>){
        this.productList = ArrayList()
        this.productList = productListArray
        notifyDataSetChanged()
    }
}