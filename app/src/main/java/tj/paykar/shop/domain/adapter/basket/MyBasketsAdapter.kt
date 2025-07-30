package tj.paykar.shop.domain.adapter.basket

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.databinding.ItemMyBasketsBinding
import tj.paykar.shop.presentation.shop.basket.MyBasketsProductsActivity

class MyBasketsAdapter(context: Context): RecyclerView.Adapter<MyBasketsAdapter.MyBasketsViewHolder>() {

    var myBasketsList = ArrayList<MyBasketModel>()

    inner class MyBasketsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemMyBasketsBinding.bind(view)

        fun bind(myBasketModel: MyBasketModel) = with(binding){
            basketName.text = myBasketModel.name
            basketDescription.text = myBasketModel.description

        }

        init {
            view.setOnClickListener {
                val position: Int = adapterPosition
                val intent = Intent(view.context, MyBasketsProductsActivity::class.java)
                intent.putExtra("basketName", myBasketsList[position].name)
                intent.putExtra("basketDescription", myBasketsList[position].description)
                intent.putExtra("index",position)
                view.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBasketsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_my_baskets, parent, false)
        return MyBasketsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBasketsViewHolder, position: Int) {
        return holder.bind(myBasketsList[position])
    }

    override fun getItemCount(): Int {
        return myBasketsList.size
    }
}