package tj.paykar.shop.domain.adapter.basket

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.ProductItems
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemChequeListBinding

class ChequeListAdapter (context: Context): RecyclerView.Adapter<ChequeListAdapter.ChequeListHolder>()  {

    private var basketProductList = ArrayList<ProductItems>()
    private var mContext = context

    class ChequeListHolder(view: View): RecyclerView.ViewHolder(view) {
        val biding = ItemChequeListBinding.bind(view)

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        fun bind(basketProductItem: ProductItems) = with(biding) {

            val quan = basketProductItem.basket_quan?.toDouble()!!
            val quanToInt = quan.toInt()
            val priceToDoble = basketProductItem.price?.toDouble()!!
            val totalPrice = priceToDoble * quan

            val name = Html.fromHtml(basketProductItem.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "${String.format("%.2f", totalPrice)} сомони"
            quantity.text = "$quanToInt ${basketProductItem.baseUnit}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChequeListHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_cheque_list, parent, false)
        return ChequeListHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ChequeListHolder, position: Int) {
        return holder.bind(basketProductList[position])
    }

    override fun getItemCount(): Int {
        return basketProductList.size
    }

    fun updateData() {
        this.basketProductList = UserStorageData(mContext).getBasketProductList() ?: ArrayList()
    }

}