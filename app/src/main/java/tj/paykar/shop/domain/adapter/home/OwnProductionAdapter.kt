package tj.paykar.shop.domain.adapter.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.Sections
import tj.paykar.shop.databinding.ItemOwnProductionBinding
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.shop.catalog.CatalogSectionProductActivity

class OwnProductionAdapter(val context: Context, val sectionList: ArrayList<Sections>): RecyclerView.Adapter<OwnProductionAdapter.ProductionViewHolder>() {

    inner class ProductionViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemOwnProductionBinding.bind(view)

        fun bind(section: Sections) = with(binding){
            Glide.with(context)
                .load("https://paykar.shop" + section.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(sectionImage)

            sectionName.text = section.name
        }

        init {
            view.setOnClickListener { it: View ->
                addBlinkEffect(it)
                val position: Int = adapterPosition
                val intent = Intent(view.context, CatalogSectionProductActivity::class.java)
                intent.putExtra("sectionId", sectionList[position].id?.toInt())
                intent.putExtra("sectionName", sectionList[position].name)
                view.context.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_own_production, parent, false)
        return ProductionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sectionList.size
    }

    override fun onBindViewHolder(holder: ProductionViewHolder, position: Int) {
        holder.bind(sectionList[position])
    }

}