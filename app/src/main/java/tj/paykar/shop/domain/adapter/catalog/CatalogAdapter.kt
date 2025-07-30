package tj.paykar.shop.domain.adapter.catalog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.CatalogModel
import tj.paykar.shop.databinding.ItemCatalogBinding

class CatalogAdapter(private val catalogList: ArrayList<CatalogModel>): RecyclerView.Adapter<CatalogAdapter.CatalogHolder>() {
    private lateinit var context: Context
    class CatalogHolder(item: View): RecyclerView.ViewHolder(item) {

        val biding = ItemCatalogBinding.bind(item)

        var sectionNameOut = ""
        var sectionId = 0

        fun bind(catalog: CatalogModel, context: Context) = with(biding) {
            sectionName.text = catalog.sectionName
            sectionNameOut = catalog.sectionName.toString()
            sectionId = catalog.sectionId?.toInt() ?: 0
            sectionsRecycler.adapter = CatalogSectionHeaderAdapter(context, catalog.sectionList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_catalog, parent, false)
        return CatalogHolder(itemView)
    }

    override fun onBindViewHolder(holder: CatalogHolder, position: Int) {
        holder.bind(catalogList[position], context)

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = (4 * context.resources.displayMetrics.density).toInt()
        layoutParams.topMargin = (4 * context.resources.displayMetrics.density).toInt()

        if (position == catalogList.size - 1) {
            layoutParams.bottomMargin = (100 * context.resources.displayMetrics.density).toInt()
        }

        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return catalogList.size
    }
}