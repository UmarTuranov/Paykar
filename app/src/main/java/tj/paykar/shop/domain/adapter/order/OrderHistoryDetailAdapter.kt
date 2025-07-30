package tj.paykar.shop.domain.adapter.order

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.ProductOrder
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemChequeListBinding

class OrderHistoryDetailAdapter (context: Context): RecyclerView.Adapter<OrderHistoryDetailAdapter.OrderDetailHolder>() {

    private var productList = ArrayList<ProductOrder>()
    private var mContext = context
    private var userId: Int = 0

    class OrderDetailHolder(view: View): RecyclerView.ViewHolder(view) {
        val biding = ItemChequeListBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(productItem: ProductOrder) = with(biding) {

            val quan = productItem.QUANTITY.toDouble()
            val priceToDoble = productItem.PRICE.toDouble()
            val totalPrice = priceToDoble * quan

            val name = Html.fromHtml(productItem.NAME, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "$totalPrice сомони"
            quantity.text = "$quan шт"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_cheque_list, parent, false)
        return OrderDetailHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailHolder, position: Int) {
        return holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun getProductList() {
        userId = UserStorageData(mContext).getUserId()
        productList = ArrayList()
        productList = UserStorageData(mContext).getHistoryProduct() ?: ArrayList()
    }
}