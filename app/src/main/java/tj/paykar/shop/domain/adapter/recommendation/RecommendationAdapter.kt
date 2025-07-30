package tj.paykar.shop.domain.adapter.recommendation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.RecommendationProductModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemRecommendationBinding
import tj.paykar.shop.domain.usecase.shop.RecommendedProductManagerService
import tj.paykar.shop.presentation.shop.catalog.ShopProductActivity

class RecommendationAdapter (context: Context): RecyclerView.Adapter<RecommendationAdapter.ProductListViewHolder>() {

    var productList = ArrayList<RecommendationProductModel>()
    private var mContext = context
    private var userId: Int = 0

    inner class ProductListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val biding = ItemRecommendationBinding.bind(view)

        @SuppressLint("SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged")
        fun bind(productItem: RecommendationProductModel, context: Context, userId: Int) = with(biding) {
            Glide.with(context)
                .load("https://paykar.shop" + productItem.detail_picture)
                .placeholder(R.drawable.nophoto)
                .transform(RoundedCorners(10))
                .into(productImg)

            productImg.clipToOutline = true

            val name = Html.fromHtml(productItem.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "${productItem.price} сомони"
        }

        init {
            biding.itemView.setOnClickListener {
                val position: Int = adapterPosition
                val intent = Intent(view.context, ShopProductActivity::class.java)
                intent.putExtra("productId", productList[position].id)
                intent.putExtra("productName", productList[position].name)
                intent.putExtra("productImage", productList[position].detail_picture)
                intent.putExtra("productPrice", productList[position].price)
                if (productList[position].wishlist) {
                    intent.putExtra("productWishlist", productList[position].wishlist)
                    intent.putExtra("productQuan", 0)
                } else {
                    intent.putExtra("productWishlist", false)
                    intent.putExtra("productQuan", productList[position].basket_quan)
                }
                intent.putExtra("productUnit", productList[position].baseUnit)
                intent.putExtra("productWishlist", productList[position].wishlist)
                intent.putExtra("productDetail", productList[position].detailText)
                intent.putExtra("nutritional", productList[position].nutritional)
                intent.putExtra("composition", productList[position].composition)
                intent.putExtra("termConditions", productList[position].termConditions)
                intent.putExtra("manufacturer", productList[position].manufacturer)
                intent.putExtra("productReviews", productList[position].reviews)
                view.context.startActivity(intent)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recommendation, parent, false)
        return ProductListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(productList[position], mContext, userId)

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == productList.size - 1) {
            layoutParams.marginEnd = (16 * mContext.resources.displayMetrics.density).toInt()
        } else {
            layoutParams.marginEnd = 0
        }
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    suspend fun sendRequest() {
        userId = UserStorageData(mContext).getUserId()
        Log.d("--D UserId", userId.toString())
        val sendReq = RecommendedProductManagerService().productList(userId)//1537
        val response: ArrayList<RecommendationProductModel> = sendReq
        productList = response
    }
}