package tj.paykar.shop.domain.adapter.wishlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.ProductItems
import tj.paykar.shop.data.model.shop.WishlistModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemWishlistBinding
import tj.paykar.shop.domain.usecase.shop.WishlistManagerService
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.authorization.LoginActivity

class WishlistAdapter(context: Context): RecyclerView.Adapter<WishlistAdapter.WishlistHolder>() {

    var whishListProduct = ArrayList<ProductItems>()
    private var mContext = context
    private var userId: Int = 0
    private var wishlist: Boolean = false

    inner class WishlistHolder(view: View, basketList: ArrayList<ProductItems>) : RecyclerView.ViewHolder(view) {
        val biding = ItemWishlistBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(context: Context, whishListProduct: ProductItems, position: Int, userId: Int) = with(biding) {

            Glide.with(context)
                .load("https://paykar.shop" + whishListProduct.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(productImg)

            productImg.clipToOutline = true

            addToWishlist.setColorFilter(context.resources.getColor(R.color.white))
            addToWishlist.background.setTint(context.resources.getColor(R.color.statusBarBackground))

            val name = Html.fromHtml(whishListProduct.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "${whishListProduct.price} сомони"
            productTitlePrice.text = "Цена за 1 шт"

            inBasketBackground.isVisible = false
            inBasket.isVisible = false

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_wishlist, parent, false)
        return WishlistHolder(view, whishListProduct)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: WishlistHolder, position: Int) {
        holder.biding.addToWishlist.setOnClickListener {
            val ptoken = PaykarIdStorage(mContext).userToken
            addBlinkEffect(it)
            if (ptoken == "") {
                MaterialAlertDialogBuilder(mContext)
                    .setTitle("Авторизация")
                    .setMessage("Чтобы использовать эту функцию необходимо пройти авторизацию")
                    .setPositiveButton("Авторизоваться") {_, _ ->
                        val intent = Intent(mContext, LoginActivity::class.java)
                        mContext.startActivity(intent)
                    }
                    .setNegativeButton("Отмена") {_, _ ->}
                    .show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        WishlistManagerService().deleteWishlist(userId, whishListProduct[position].id!!.toInt())
                        withContext(Dispatchers.Main) {

                            deleteItem(position)
                            CoroutineScope(Dispatchers.IO).launch {
                                sendRequest()
                                withContext(Dispatchers.Main) { notifyDataSetChanged() }
                            }

                            val snack = Snackbar.make(holder.biding.root, "Удалено из избранных", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                            snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                            snack.show()
                        }
                    }catch (_:Exception){}
                }
            }
        }

        holder.biding.productBasket.setOnClickListener {
            addBlinkEffect(it)
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
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        WishlistManagerService().deleteWishlist(userId, whishListProduct[position].id!!.toInt())
                        getBasketService().addBasketItem(userId, whishListProduct[position].id!!.toInt(), 1.0)
                        withContext(Dispatchers.Main) {
                            deleteItem(position)
                            CoroutineScope(Dispatchers.IO).launch {
                                sendRequest()
                                withContext(Dispatchers.Main) { notifyDataSetChanged() }
                            }
                            val snack = Snackbar.make(holder.biding.root, "Добавлен в корзину", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                            snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                            snack.show()
                        }
                    }catch (_:Exception){}
                }
            }
        }

        return holder.bind(mContext, whishListProduct[position], position, userId)
    }

    override fun getItemCount(): Int {
        return whishListProduct.size
    }

    suspend fun sendRequest() {
        userId = UserStorageData(mContext).getUserId()
        val sendReq = WishlistManagerService().wishlistItems(userId)
        val response: WishlistModel = sendReq
        val productItemList: ArrayList<ProductItems> = response.productList
        this.whishListProduct = productItemList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        whishListProduct.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                WishlistManagerService().cleanWishlist(userId)
                withContext(Dispatchers.Main) {
                    whishListProduct.clear()
                    notifyDataSetChanged()
                }
            }catch (_:Exception){}
        }
    }
}