package tj.paykar.shop.domain.adapter.basket

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.RecommendationProductModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemRecommendedSearchBinding
import tj.paykar.shop.domain.usecase.shop.WishlistManagerService
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.authorization.LoginActivity

class RecommendProductAdapter(context: Context, val clickListener: tj.paykar.shop.domain.reprository.shop.RecommendProductClickManager): RecyclerView.Adapter<RecommendProductAdapter.RecommendViewHolder>() {

    var productList = ArrayList<RecommendationProductModel>()
    private var mContext = context
    private var userId: Int = 0

    inner class RecommendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val biding = ItemRecommendedSearchBinding.bind(view)

        @SuppressLint("SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged")
        fun bind(productItem: RecommendationProductModel, context: Context, userId: Int) = with(biding) {

            Glide.with(context)
                .load("https://paykar.shop" + productItem.detail_picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(productImg)

            productImg.clipToOutline = true
            inBasketView.isVisible = false
            inBasketQuan.isVisible = false
            val name = Html.fromHtml(productItem.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "${productItem.price} сомони"

            if (productItem.wishlist) {
                inBasketQuan.text = "+"
            } else {
                if (productItem.basket_quan != 0.0 && productItem.basket_quan != null) {
                    val quanInt = if (hasDecimalPart(productItem.basket_quan!!)){
                        productItem.basket_quan
                    }
                    else {
                        productItem.basket_quan?.toInt()
                    }
                    inBasketView.isVisible = true
                    inBasketQuan.isVisible = true
                    inBasketQuan.text = "$quanInt"
                }
            }


            notSelectedLinear.setOnClickListener {
                addBlinkEffect(it)
                addProduct(productItem, biding)
            }

            addProductQuantity.setOnClickListener {
                addBlinkEffect(it)
                addProduct(productItem, biding)
            }

            minusProductQuantity.setOnClickListener {
                addBlinkEffect(it)
                val quan = inBasketQuan.text.toString()
                Log.d("--W Which", quan)
                if (quan.toDouble() > 1) {
                    Log.d("--W Which", "First")
                    minusProduct(quan, productItem, biding)
                }
                else  {
                    Log.d("--W Which", "third")
                    deleteProduct(productItem, biding)
                }
            }
        }

        private fun addProduct(productItem: RecommendationProductModel, binding: ItemRecommendedSearchBinding) {
            binding.apply {
                val ptoken = PaykarIdStorage(mContext).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(mContext)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы добавить товар в корзину необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(mContext, LoginActivity::class.java)
                            mContext.startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    if (inBasketView.isVisible) {
                        Log.d("--W Which", "first o")
                        val quan = inBasketQuan.text.toString()
                        val quanInt = quan.toInt() + 1
                        inBasketQuan.text = "$quanInt"
                        productList[adapterPosition].basket_quan = productList[adapterPosition].basket_quan?.plus(
                            1
                        )
                        inBasketView.isVisible = true
                        showViewWithAnimation(inBasketQuan)
                    }
                    else {
                        Log.d("--W Which", "second o")
                        inBasketQuan.text = "1"
                        productList[adapterPosition].basket_quan = 1.0
                        showViewWithAnimation(inBasketView)
                        showViewWithAnimation(inBasketQuan)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            if (productItem.wishlist) {
                                WishlistManagerService().deleteWishlist(userId, productItem.id.toInt())
                            }
                            val res = getBasketService().addBasketItem(userId, productItem.id.toInt(), 1.0)
                            val basketCount = getBasketService().basketCount(userId)
                            withContext(Dispatchers.Main) {
                                if (res.isSuccessful) {
                                    Log.d("--R Request", res.body().toString())
                                    Log.d("--R Request", res.toString())
                                    UserStorageData(mContext).saveBasketCount(basketCount.count)
                                }
                            }
                        }catch (_:Exception){
                            withContext(Dispatchers.Main){
                                val snack = Snackbar.make(biding.root, "Сервер недоступен", Snackbar.LENGTH_LONG)
                                snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                                snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                                snack.show()
                            }
                        }
                    }
                }
            }
        }

        private fun minusProduct(quan: String, productItem: RecommendationProductModel, binding: ItemRecommendedSearchBinding) {
            binding.apply {
                if (inBasketView.isVisible) {
                    val quanInt = quan.toInt() - 1
                    inBasketQuan.text = "$quanInt"
                    productList[adapterPosition].basket_quan = productList[adapterPosition].basket_quan?.minus(
                        1
                    )
                }
                inBasketView.isVisible = true
                showViewWithAnimation(inBasketQuan)
                notSelectedLinear.isGone = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (productItem.wishlist) {
                            WishlistManagerService().deleteWishlist(userId, productItem.id.toInt())
                        }
                        val res = getBasketService().addBasketItem(userId, productItem.id.toInt(), -1.0)
                        val basketCount = getBasketService().basketCount(userId)
                        withContext(Dispatchers.Main) {
                            if (res.isSuccessful) {
                                Log.d("--R Request", res.body().toString())
                                Log.d("--R Request", res.toString())
                                UserStorageData(mContext).saveBasketCount(basketCount.count)
                            }
                        }
                    }catch (_:Exception){
                        withContext(Dispatchers.Main){
                            val snack = Snackbar.make(biding.root, "Сервер недоступен", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                            snack.show()
                        }
                    }
                }
            }
        }

        private fun deleteProduct(productItem: RecommendationProductModel, binding: ItemRecommendedSearchBinding) {
            binding.apply {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        getBasketService().deleteBasketItem(userId, productItem.id.toInt())
                        withContext(Dispatchers.Main) {
                            showViewWithAnimation(notSelectedLinear)
                            HideViewWithAnimation().goneViewWithAnimation(inBasketView)
                            inBasketQuan.text = "0"
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder {
        userId = UserStorageData(mContext).getUserId()
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recommended_search, parent, false)
        return RecommendViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
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

    private fun hasDecimalPart(number: Double): Boolean {
        return number % 1 != 0.0
    }
}