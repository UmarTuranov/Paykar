package tj.paykar.shop.domain.adapter.catalog

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.Sections
import tj.paykar.shop.databinding.ItemCatalogSectionHeaderBinding
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.shop.catalog.CatalogSectionProductActivity

class CatalogSectionHeaderAdapter(context: Context, items: ArrayList<Sections>?): RecyclerView.Adapter<CatalogSectionHeaderAdapter.SectionHolder>() {

    var sectionList: ArrayList<Sections>? = items
    private val context: Context

    init {
        this.context = context
    }

    inner class SectionHolder(view: View) : RecyclerView.ViewHolder(view) {

        val biding = ItemCatalogSectionHeaderBinding.bind(view)
        fun bind(sectionItem: Sections, context: Context) = with(biding) {
            sectionName.text = sectionItem.name
            Glide.with(context)
                .load("https://paykar.shop" + sectionItem.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(sectionImage)
        }

        init {

            view.setOnClickListener { itemView: View ->
                addBlinkEffect(itemView)
                val position: Int = adapterPosition
                val intent = Intent(view.context, CatalogSectionProductActivity::class.java)
                intent.putExtra("sectionId", sectionList?.get(position)?.id?.toInt())
                intent.putExtra("sectionName", sectionList?.get(position)?.name)
                view.context.startActivity(intent)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_catalog_section_header, parent, false)
        return SectionHolder(view)
    }

    override fun onBindViewHolder(holder: SectionHolder, position: Int) {
        holder.bind(sectionList!![position], context)
//        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
//        if (position == sectionList?.size?.minus(1)) {
//            layoutParams.marginEnd = (16 * context.resources.displayMetrics.density).toInt()
//        } else {
//            layoutParams.marginEnd = 0
//        }
//        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return sectionList!!.size
    }

}