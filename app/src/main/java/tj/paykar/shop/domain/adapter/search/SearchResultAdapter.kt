package tj.paykar.shop.domain.adapter.search

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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel
import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.data.storage.MyBasketStorage
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemCatalogSectionProductBinding
import tj.paykar.shop.domain.usecase.shop.SearchManagerService
import tj.paykar.shop.domain.usecase.shop.WishlistManagerService
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.authorization.LoginActivity
import tj.paykar.shop.presentation.shop.basket.BasketActivity
import tj.paykar.shop.presentation.shop.catalog.ShopProductActivity
import tj.paykar.shop.presentation.shop.wishlist.WishlistActivity

class SearchResultAdapter(context: Context): RecyclerView.Adapter<SearchResultAdapter.ProductListViewHolder>() {

    var productList = ArrayList<CatalogSectionProductModel>()
    private var mContext = context
    private var userId: Int = 0
    private var wishlist = false
    private var searchTextIn = ""
    private var productInMyBaskets: Boolean = false
    private var myBasketList: ArrayList<MyBasketModel> = arrayListOf()
    private var basketHaveProductName: String = ""

    inner class ProductListViewHolder(view: View, productItemList: ArrayList<CatalogSectionProductModel>) : RecyclerView.ViewHolder(view) {

        val biding = ItemCatalogSectionProductBinding.bind(view)

        @SuppressLint("SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged")
        fun bind(productItem: CatalogSectionProductModel, position: Int, context: Context, userId: Int) = with(biding) {

            myBasketList = MyBasketStorage(mContext).getBasketLists()

            Glide.with(context)
                .load("https://paykar.shop" + productItem.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(productImg)

            productImg.clipToOutline = true

            inBasket.isVisible = false

            val name = Html.fromHtml(productItem.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "${productItem.price} сомони"
            addWishlist.setColorFilter(context.resources.getColor(R.color.black_to_white))
            addWishlist.background.setTint(context.resources.getColor(R.color.greyOpacity))

            if (productItem.basket_quan != 0.0) {
                inBasket.isVisible = true
            }

            productTitlePrice.text = "Цена за 1 ${productItem.baseUnit}"

            if (productList[position].wishlist){
                addWishlist.setColorFilter(context.resources.getColor(R.color.white))
                addWishlist.background.setTint(context.resources.getColor(R.color.statusBarBackground))
            } else {
                addWishlist.setColorFilter(context.resources.getColor(R.color.black_to_white))
                addWishlist.background.setTint(context.resources.getColor(R.color.greyOpacity))
            }

            addWishlist.setOnClickListener {
                addBlinkEffect(it)
                val ptoken = PaykarIdStorage(mContext).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы добавить товар в избранную необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ -> }
                        .show()
                } else {
                    basketHaveProductName = ""
                    if (myBasketList.isNotEmpty()){
                        for (i in myBasketList){
                            if (i.products!!.isNotEmpty()){
                                for (p in i.products){
                                    if (productItem.id == p!!.id){
                                        productInMyBaskets = true
                                        basketHaveProductName += "${i.name}, "
                                    }
                                }
                            }
                        }
                    }

                    if (productList[position].wishlist){
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                WishlistManagerService().deleteWishlist(userId, productItem.id.toInt())
                                sendRequest(searchTextIn)
                                withContext(Dispatchers.Main) {
                                    notifyDataSetChanged()
                                    addWishlist.setColorFilter(context.resources.getColor(R.color.black_to_white))
                                    addWishlist.background.setTint(context.resources.getColor(R.color.greyOpacity))
                                    wishlist = false
                                    val snack = Snackbar.make(biding.root, "Удален из избранных", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(context, R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(context, R.color.white))
                                    //snack.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                                    //snack.setAction("Посмотреть избранные") { }
                                    snack.show()
                                }
                            }catch (_:Exception){}
                        }
                    } else {

                        if (productInMyBaskets){
                            MaterialAlertDialogBuilder(mContext)
                                .setTitle("Товар находится в корзине ${basketHaveProductName.removeRange(basketHaveProductName.length-2 until basketHaveProductName.length)}")
                                .setMessage("Если вы добавите этот товар в избранные, он удалится из ваших корзин")
                                .setPositiveButton("Добавить в избранные"){_,_ ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            for (i in myBasketList){
                                                if (i.products!!.isNotEmpty()){
                                                    for (p in i.products){
                                                        if (productItem.id == p!!.id){
                                                            MyBasketStorage(mContext).removeProductFromBasket(myBasketList.indexOf(i), i.products.indexOf(p))
                                                        }
                                                    }
                                                }
                                            }
                                            WishlistManagerService().addToWishlist(userId, productItem.id.toInt(), 1)
                                            sendRequest(searchTextIn)
                                            withContext(Dispatchers.Main) {
                                                notifyDataSetChanged()
                                                addWishlist.setColorFilter(context.resources.getColor(R.color.white))
                                                addWishlist.background.setTint(context.resources.getColor(R.color.statusBarBackground))
                                                wishlist = true
                                                val snack = Snackbar.make(biding.root, "Добавлен в избранное", Snackbar.LENGTH_LONG)
                                                snack.setBackgroundTint(ContextCompat.getColor(context, R.color.statusBarBackground))
                                                snack.setTextColor(ContextCompat.getColor(context, R.color.white))
                                                snack.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                                                snack.setAction("Посмотреть избранные") {
                                                    val intent = Intent(context, WishlistActivity::class.java)
                                                    context.startActivity(intent)
                                                }
                                                snack.show()
                                            }
                                        }catch (_:Exception){}
                                    }
                                }
                                .setNegativeButton("Отменить"){_,_ ->}
                                .show()
                        }else{
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    WishlistManagerService().addToWishlist(userId, productItem.id.toInt(), 1)
                                    sendRequest(searchTextIn)
                                    withContext(Dispatchers.Main) {
                                        notifyDataSetChanged()
                                        addWishlist.setColorFilter(context.resources.getColor(R.color.white))
                                        addWishlist.background.setTint(context.resources.getColor(R.color.statusBarBackground))
                                        wishlist = true
                                        val snack = Snackbar.make(biding.root, "Добавлен в избранное", Snackbar.LENGTH_LONG)
                                        snack.setBackgroundTint(ContextCompat.getColor(context, R.color.statusBarBackground))
                                        snack.setTextColor(ContextCompat.getColor(context, R.color.white))
                                        snack.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                                        snack.setAction("Посмотреть избранные") {
                                            val intent = Intent(context, WishlistActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                        snack.show()
                                    }
                                }catch (_:Exception){}
                            }
                        }
                    }
                }
            }

            productBasket.setOnClickListener {
                addBlinkEffect(it)
                val ptoken = PaykarIdStorage(mContext).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы добавить товар в корзину необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    if (productList[position].wishlist) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                WishlistManagerService().deleteWishlist(userId, productItem.id.toInt())
                                getBasketService().addBasketItem(userId, productItem.id.toInt(), 1.0)
                                sendRequest(searchTextIn)
                                withContext(Dispatchers.Main) {
                                    val basketCount = getBasketService().basketCount(userId)
                                    UserStorageData(context).saveBasketCount(basketCount.count)
                                    notifyDataSetChanged()
                                    addWishlist.setColorFilter(context.resources.getColor(R.color.black_to_white))
                                    addWishlist.background.setTint(context.resources.getColor(R.color.greyOpacity))

                                    inBasket.isVisible = true

                                    val snack =
                                        Snackbar.make(
                                            biding.root,
                                            "Добавлен в корзину",
                                            Snackbar.LENGTH_LONG
                                        )
                                    snack.setBackgroundTint(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.statusBarBackground
                                        )
                                    )
                                    snack.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.white
                                        )
                                    )
                                    snack.setActionTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.white
                                        )
                                    )
                                    snack.setAction("Посмотреть корзину") {
                                        val intent = Intent(context, BasketActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    snack.show()

                                }
                            }catch (_:Exception){}
                        }

                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                getBasketService().addBasketItem(userId, productItem.id.toInt(), 1.0)
                                sendRequest(searchTextIn)
                                withContext(Dispatchers.Main) {
                                    val basketCount = getBasketService().basketCount(userId)
                                    UserStorageData(context).saveBasketCount(basketCount.count)
                                    notifyDataSetChanged()

                                    inBasket.isVisible = true

                                    val snack =
                                        Snackbar.make(
                                            biding.root,
                                            "Добавлен в корзину",
                                            Snackbar.LENGTH_LONG
                                        )
                                    snack.setBackgroundTint(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.statusBarBackground
                                        )
                                    )
                                    snack.setTextColor(ContextCompat.getColor(context, R.color.white))
                                    snack.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                                    snack.setAction("Посмотреть корзину") {
                                        val intent = Intent(context, BasketActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    snack.show()

                                }
                            }catch (_:Exception){}
                        }
                    }
                }
            }
        }

        init {

            view.setOnClickListener {
                val position: Int = adapterPosition
                val intent = Intent(view.context, ShopProductActivity::class.java)
                intent.putExtra("productId", productList[position].id)
                intent.putExtra("productName", productList[position].name)
                intent.putExtra("productImage", productList[position].pictureDetail)
                intent.putExtra("productPrice", productList[position].price)
                intent.putExtra("productQuan", productList[position].basket_quan)
                intent.putExtra("productUnit", productList[position].baseUnit)
                intent.putExtra("productWishlist", productList[position].wishlist)
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
        val view = inflater.inflate(R.layout.item_catalog_section_product, parent, false)
        return ProductListViewHolder(view, productList)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(productList[position], position, mContext, userId)

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = (8 * mContext.resources.displayMetrics.density).toInt()
        layoutParams.topMargin = (8 * mContext.resources.displayMetrics.density).toInt()

        if (position == productList.size - 1) {
            layoutParams.bottomMargin = (100 * mContext.resources.displayMetrics.density).toInt()
        }

        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    suspend fun sendRequest(searchText: String) {
        userId = UserStorageData(mContext).getUserId()
        searchTextIn = searchText
        val sendReq = SearchManagerService().searchProductList(userId, searchText)
        val response: ArrayList<CatalogSectionProductModel> = sendReq
        this.productList = response
    }
}