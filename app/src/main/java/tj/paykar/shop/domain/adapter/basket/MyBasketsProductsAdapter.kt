package tj.paykar.shop.domain.adapter.basket

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.MyBasketsProductModel
import tj.paykar.shop.data.storage.MyBasketStorage
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemMyBasketsProductsBinding
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.presentation.authorization.LoginActivity
import tj.paykar.shop.presentation.shop.basket.BasketActivity

class MyBasketsProductsAdapter(context: Context, basketIndex: Int): RecyclerView.Adapter<MyBasketsProductsAdapter.MyBasketsProductsViewHolder>() {

    var myBasketsProductsList = ArrayList<MyBasketsProductModel?>()
    private var mContext = context
    private var listIndex = basketIndex
    var userId: Int = 0

    inner class MyBasketsProductsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemMyBasketsProductsBinding.bind(view)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(productItem: MyBasketsProductModel, position: Int, context: Context, userId: Int) = with(binding){

            Glide.with(context)
                .load("https://paykar.shop" + productItem.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(productImg)

            productImg.clipToOutline = true

            val name = Html.fromHtml(productItem.name, Html.FROM_HTML_MODE_LEGACY).toString()
            productName.text = name
            productPrice.text = "${productItem.price} сомони"

            productBasket.setOnClickListener {
                val ptoken = PaykarIdStorage(context).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы добавить товар в корзину необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_,_ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            getBasketService().addBasketItem(userId, productItem.id.toInt(), 1.0)
                            withContext(Dispatchers.Main) {
                                val basketCount = getBasketService().basketCount(userId)
                                UserStorageData(context).saveBasketCount(basketCount.count)
                                val snack = Snackbar.make(
                                    binding.root,
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

            deleteFromBasket.setOnClickListener {
                try {
                    MyBasketStorage(mContext).removeProductFromBasket(listIndex, position)
                    myBasketsProductsList.removeAt(position)
                    notifyDataSetChanged()

                    val snack = Snackbar.make(binding.root, "Удалено из корзины", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    snack.show()
                }catch (_:Exception){}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBasketsProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_my_baskets_products, parent, false)
        return MyBasketsProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBasketsProductsViewHolder, position: Int) {
        return holder.bind(myBasketsProductsList[position]!!, position, mContext, userId)
    }

    override fun getItemCount(): Int {
        return myBasketsProductsList.size
    }
}