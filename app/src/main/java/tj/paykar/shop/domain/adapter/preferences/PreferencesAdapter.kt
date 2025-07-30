package tj.paykar.shop.domain.adapter.preferences

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.PreferencesModel
import tj.paykar.shop.databinding.ItemPreferencesBinding
import tj.paykar.shop.domain.reprository.shop.PreferencesItemClickManager
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation

class PreferencesAdapter(private val context: Context, private val clickManager: tj.paykar.shop.domain.reprository.shop.PreferencesItemClickManager): RecyclerView.Adapter<PreferencesAdapter.PreferencesViewHolder>() {

    var preferencesList = ArrayList<PreferencesModel>()
    inner class PreferencesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var binding = ItemPreferencesBinding.bind(view)
        fun bind(item: PreferencesModel, position: Int) = with(binding){
            preferencesName.text = item.name
            if (item.selected == true) {
                preferencesCV.setCardBackgroundColor(context.getColor(R.color.green))
                preferencesName.setTextColor(Color.WHITE)
            }
            preferencesCV.setOnClickListener {
                preferencesList[position].selected = !(preferencesList[position].selected ?: false)
                clickManager.checkSelected(preferencesList)
                if(preferencesList[position].selected == true){
                    preferencesCV.setCardBackgroundColor(context.getColor(R.color.green))
                    preferencesName.setTextColor(Color.WHITE)
                }
                else {
                    preferencesCV.setCardBackgroundColor(Color.TRANSPARENT)
                    preferencesName.setTextColor(context.getColor(R.color.green))
                }
                showViewWithAnimation(it, 500)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferencesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_preferences, parent, false)
        return PreferencesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return preferencesList.size
    }

    override fun onBindViewHolder(holder: PreferencesViewHolder, position: Int) {
        return holder.bind(preferencesList[position], position)
    }
}