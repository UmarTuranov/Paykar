package tj.paykar.shop.domain.adapter.basket

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.data.model.shop.MyBasketsProductModel
import tj.paykar.shop.data.storage.MyBasketStorage
import tj.paykar.shop.databinding.ItemMyBasketsListBinding
import tj.paykar.shop.presentation.shop.basket.MyBasketsProductsActivity

class MyBasketsListAdapter(context: Context, product: MyBasketsProductModel): RecyclerView.Adapter<MyBasketsListAdapter.MyBasketsListViewHolder>(){

    var myBasketsList = ArrayList<MyBasketModel>()
    val mContext = context
    val mProduct: MyBasketsProductModel = product
    var indexArray = ArrayList<Int>()

    inner class MyBasketsListViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val binding = ItemMyBasketsListBinding.bind(view)

        @SuppressLint("ResourceAsColor")
        fun bind(myBasketModel: MyBasketModel) = with(binding){
            basketName.text = myBasketModel.name
            basketDescription.text = myBasketModel.description
            val position: Int = adapterPosition

            var productInBasket: Boolean = false

            if (indexArray.isNotEmpty()){
                for (i in indexArray){
                    if(position == i){
                        productInBasket = true
                        myImageView.setImageResource(R.drawable.ic_right)
                    }else{
                        myImageView.setImageResource(R.drawable.ic_basket_add)
                    }
                }
            }

            itemView.setOnClickListener {
                if (!productInBasket){
                    MyBasketStorage(mContext).addProductToTheBasket(mProduct, position)
                    productInBasket = true
                    myImageView.setImageResource(R.drawable.ic_right)
                    val snack = Snackbar.make(binding.root, "Добавлено в корзину", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    snack.view.layoutParams = (snack.view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                        gravity = Gravity.CENTER
                    }
                    snack.show()
                }else{
                    val intent = Intent(mContext, MyBasketsProductsActivity::class.java)
                    intent.putExtra("basketName", myBasketModel.name)
                    intent.putExtra("basketDescription", myBasketModel.description)
                    intent.putExtra("index", position)
                    intent.putExtra("reason", "has")
                    mContext.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBasketsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_my_baskets_list, parent, false)
        return MyBasketsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBasketsListViewHolder, position: Int) {
        return holder.bind(myBasketsList[position])
    }

    override fun getItemCount(): Int {
        return myBasketsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun nDSCH(){
        notifyDataSetChanged()
    }
}