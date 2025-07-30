package tj.paykar.shop.domain.adapter.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.paykar.shop.R
import tj.paykar.shop.data.model.VacanciesModel
import tj.paykar.shop.databinding.ItemVacanciesBinding
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.webview.WebViewActivity

class VacanciesAdapter(val context: Context, val vacanciesList: ArrayList<VacanciesModel>): RecyclerView.Adapter<VacanciesAdapter.VacanciesViewHolder>() {
    inner class VacanciesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemVacanciesBinding.bind(view)
        fun bind(vacancy: VacanciesModel) = with(binding){
            Glide.with(context)
                .load(vacancy.picture)
                .centerCrop()
                .placeholder(R.drawable.nophoto)
                .into(sectionImage)

            sectionName.text = vacancy.name
        }

        init {
            view.setOnClickListener { it: View ->
                addBlinkEffect(it)
                val position: Int = adapterPosition
                val intent = Intent(view.context, WebViewActivity::class.java)
                intent.putExtra("webTitle", vacanciesList[position].name)
                intent.putExtra("webUrl", vacanciesList[position].link)
                view.context.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacanciesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_vacancies, parent, false)
        return VacanciesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vacanciesList.size
    }

    override fun onBindViewHolder(holder: VacanciesViewHolder, position: Int) {
        holder.bind(vacanciesList[position])
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == vacanciesList.size - 1) {
            layoutParams.marginEnd = (16 * context.resources.displayMetrics.density).toInt()
        } else {
            layoutParams.marginEnd = 0
        }
        holder.itemView.layoutParams = layoutParams
    }

}