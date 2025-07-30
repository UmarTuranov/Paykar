package tj.paykar.shop.domain.adapter.basket

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.BasketModel
import tj.paykar.shop.data.model.shop.ProductItems
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemBasketBinding
import tj.paykar.shop.domain.usecase.shop.getBasketService


class BasketAdapter (context: Context, private var onItemClicked: ((totalPrice: String, count: Int) -> Unit)): RecyclerView.Adapter<BasketAdapter.BasketHolder>() {

    var basketResposne: BasketModel = BasketModel("0", "0", "0", ArrayList<ProductItems>())
    var basketProductList = ArrayList<ProductItems>()
    private var mContext = context
    private var basketTotalPrice: String = ""
    var totalPrice: Double = 0.0
    private var userId: Int = 0

    inner class BasketHolder(view: View, basketList: ArrayList<ProductItems>): RecyclerView.ViewHolder(view) {

        val biding = ItemBasketBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(basketProductItem: ProductItems, context: Context, userId: Int) = with(biding) {

            Glide.with(context)
                .load("https://paykar.shop" + basketProductItem.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(productImg)

            productImg.clipToOutline = true
            val quan = basketProductItem.basket_quan?.toDouble()!!
            Log.d("--B Quan", quan.toString())
            val quanToInt = quan.toInt()

            val name = Html.fromHtml(basketProductItem.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name

            if (basketProductItem.baseUnit == "шт"){
                inBasket.text = "$quanToInt"
                productTitlePrice.text = "Цена за $quanToInt ${basketProductItem.baseUnit}"
                productPrice.text = "${String.format("%.2f", basketProductItem.price?.toDouble()?.times(quanToInt) ?: 0.0)} с."
            }else{
                inBasket.text = String.format("%.1f", quan)
                productTitlePrice.text = "Цена за ${String.format("%.1f", quan)} ${basketProductItem.baseUnit}"
                productPrice.text = "${String.format("%.2f", basketProductItem.price?.toDouble()?.times(quan) ?: 0.0)} с."
            }

            deleteBasketItem.setColorFilter(context.getResources().getColor(R.color.red))

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_basket, parent, false)
        return BasketHolder(view, basketProductList)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BasketHolder, position: Int) {

        holder.biding.deleteBasketItem.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    getBasketService().deleteBasketItem(userId, basketProductList[position].id!!.toInt())
                    withContext(Dispatchers.Main) {
                        deleteItem(position)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                sendRequest()
                                withContext(Dispatchers.Main) { notifyDataSetChanged() }
                            }catch (_:Exception){}
                        }
                        val snack = Snackbar.make(holder.biding.root, "Удалено из корзины", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                        snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                        //snack.setAction("Отменить") { }
                        snack.show()
                    }
                }catch (_:Exception){}
            }
        }

        holder.biding.productBasket.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var quan = basketProductList[position].basket_quan?.toDouble()!!
                    val addQuantity = if (basketProductList[position].baseUnit == "шт"){
                        1.0
                    }else{
                        0.1
                    }
                    var quanToInt = quan.toInt()
                    var roundedQuan = 0.0
                    getBasketService().addBasketItem(userId, basketProductList[position].id!!.toInt(), addQuantity)
                    val basketCount = getBasketService().basketCount(userId)
                    UserStorageData(mContext).saveBasketCount(basketCount.count)
                    withContext(Dispatchers.Main) {
                        if (basketProductList[position].baseUnit == "шт"){
                            quanToInt += 1
                            holder.biding.inBasket.text = "$quanToInt"
                            addItem(position)
                        }else{
                            quan += 0.1
                            roundedQuan = Math.round(quan * 10.0) / 10.0
                            holder.biding.inBasket.text = "$roundedQuan"
                            addItemKg(position)
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                sendRequest()
                                withContext(Dispatchers.Main) { notifyDataSetChanged() }
                            }catch (_:Exception){}
                        }
                        val snack = Snackbar.make(holder.biding.root, "Добавлено в корзину", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                        snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                        //snack.setAction("Отменить") { }
                        snack.show()
                    }
                }catch (_:Exception){}
            }

        }

        holder.biding.productBasketMinus.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var quan = basketProductList[position].basket_quan?.toDouble()!!
                    val addQuantity = if (basketProductList[position].baseUnit == "шт"){
                        -1.0
                    }else{
                        -0.1
                    }
                    var quanToInt = quan.toInt()
                    var roundedQuan = 0.0
                    getBasketService().addBasketItem(userId, basketProductList[position].id!!.toInt(), addQuantity)
                    val basketCount = getBasketService().basketCount(userId)
                    UserStorageData(mContext).saveBasketCount(basketCount.count)
                    basketProductList[position].basket_quan = "${basketProductList[position].basket_quan?.toDouble()?.plus(addQuantity)}"
                    withContext(Dispatchers.Main) {
                        if (basketProductList[position].baseUnit == "шт"){
                            if (quanToInt > 1){
                                quanToInt -= 1
                                holder.biding.inBasket.text = "$quanToInt"
                                deleteOneItem(position)
                            }else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        getBasketService().deleteBasketItem(userId, basketProductList[position].id!!.toInt())
                                        withContext(Dispatchers.Main) {
                                            deleteItem(position)
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    sendRequest()
                                                    withContext(Dispatchers.Main) { notifyDataSetChanged() }
                                                }catch (_:Exception){}
                                            }
                                            val snack = Snackbar.make(holder.biding.root, "Удалено из корзины", Snackbar.LENGTH_LONG)
                                            snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                                            snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                                            snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                                            //snack.setAction("Отменить") { }
                                            snack.show()
                                        }
                                    } catch (_:Exception){}
                                }
                            }
                        }
                        else{
                            if (quan > 0.1){
                                quan -= 0.1
                                roundedQuan = Math.round(quan * 10.0) / 10.0
                                holder.biding.inBasket.text = "$roundedQuan"
                                deleteOneItemKg(position)
                            }else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        getBasketService().deleteBasketItem(userId, basketProductList[position].id!!.toInt())
                                        withContext(Dispatchers.Main) {
                                            deleteItem(position)
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    sendRequest()
                                                    withContext(Dispatchers.Main) { notifyDataSetChanged() }
                                                }catch (_:Exception){}
                                            }
                                            val snack = Snackbar.make(holder.biding.root, "Удалено из корзины", Snackbar.LENGTH_LONG)
                                            snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                                            snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                                            snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                                            //snack.setAction("Отменить") { }
                                            snack.show()
                                        }
                                    }catch (_:Exception){}
                                }
                            }
                        }
//                        CoroutineScope(Dispatchers.IO).launch {
//                            try {
//                                sendRequest()
//                                withContext(Dispatchers.Main) { notifyDataSetChanged() }
//                            }catch (_:Exception){}
//                        }
                        val snack = Snackbar.make(holder.biding.root, "Добавлено в корзину", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                        snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                        //snack.setAction("Отменить") { }
                        snack.show()
                    }
                }catch (e:Exception){ Log.d("--E BasketError", e.toString())}
            }

        }

        return holder.bind(basketProductList[position], mContext, userId)
    }

    override fun getItemCount(): Int {
        return basketProductList.size
    }

    suspend fun sendRequest() {
        userId = UserStorageData(mContext).getUserId()
        Log.d("User Id", userId.toString())
        val sendReq = getBasketService().basketItems(userId)
        Log.d("Your Basket Model", sendReq.toString())
        val response: BasketModel = sendReq
        val productItemList: ArrayList<ProductItems> = response.productList
        Log.d("Your Basket List", productItemList.toString())
        this.basketResposne = response
        this.basketProductList = productItemList
        //Сохранение списка продуктов
        UserStorageData(mContext).saveBasketProductList(productItemList)
        if (productItemList.isEmpty()) {
            UserStorageData(mContext).saveBasketCount(0)
        }
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun deleteItem(index: Int) {
        if (basketProductList.isNotEmpty()){
            val price = basketProductList[index].price!!.toDouble()
            val quan = basketProductList[index].basket_quan!!.toDouble()

            if (basketTotalPrice == "") {
                totalPrice = basketResposne.totalPrice?.toDouble()!!
            }

            totalPrice -= (price * quan)
            basketTotalPrice = String.format("%.2f", totalPrice)
            basketProductList.removeAt(index)
            notifyItemRemoved(index)
            onItemClicked(basketTotalPrice, basketProductList.size)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(index: Int) {
        val price = basketProductList[index].price!!.toDouble()
        if (basketTotalPrice == "") {
            totalPrice = basketResposne.totalPrice?.toDouble()!!
        }
        totalPrice += price
        basketTotalPrice = String.format("%.2f", totalPrice)
        onItemClicked(basketTotalPrice, basketProductList.size)
        //notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteOneItem(index: Int) {
        val price = basketProductList[index].price!!.toDouble()
        if (basketTotalPrice == "") {
            totalPrice = basketResposne.totalPrice?.toDouble()!!
        }
        totalPrice -= price
        basketTotalPrice = String.format("%.2f", totalPrice)
        onItemClicked(basketTotalPrice, basketProductList.size)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItemKg(index: Int) {
        val price = basketProductList[index].price!!.toDouble()
        if (basketTotalPrice == "") {
            totalPrice = basketResposne.totalPrice?.toDouble()!!
        }
        totalPrice += price * 0.1
        basketTotalPrice = String.format("%.2f", totalPrice)
        onItemClicked(basketTotalPrice, basketProductList.size)
        //notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteOneItemKg(index: Int) {
        try {
            val price = basketProductList[index].price!!.toDouble()
            if (basketTotalPrice == "") {
                totalPrice = basketResposne.totalPrice?.toDouble()!!
            }
            totalPrice -= price * 0.1
            basketTotalPrice = String.format("%.2f", totalPrice)
            onItemClicked(basketTotalPrice, basketProductList.size)
            notifyDataSetChanged()
        }catch (_:Exception){}
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getBasketService().cleanBasketItems(userId)
                withContext(Dispatchers.Main) {
                    basketProductList.clear()
                    notifyDataSetChanged()
                }
            }catch (_:Exception){}
        }
    }

}