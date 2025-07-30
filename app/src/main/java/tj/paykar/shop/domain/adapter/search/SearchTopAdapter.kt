package tj.paykar.shop.domain.adapter.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ItemTopSearchBinding
import tj.paykar.shop.domain.usecase.shop.SearchManagerService
import tj.paykar.shop.presentation.shop.search.SearchResultActivity


class SearchTopAdapter(context: Context): RecyclerView.Adapter<SearchTopAdapter.TopHolder>() {

    private var queryList = ArrayList<String>()

    inner class TopHolder(item: View) : RecyclerView.ViewHolder(item) {

        val binding = ItemTopSearchBinding.bind(item)

        fun bind(searchItem: String) = with(binding) {
            searchQuery.text = searchItem
        }

        init {

            item.setOnClickListener {
                val position: Int = adapterPosition
                val intent = Intent(it.context, SearchResultActivity::class.java)
                intent.putExtra("query", queryList[position])
                it.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_top_search, parent, false)
        return TopHolder(view)
    }

    override fun onBindViewHolder(holder: TopHolder, position: Int) {
        return holder.bind(queryList[position])
    }

    override fun getItemCount(): Int {
        return queryList.size
    }

    suspend fun sendRequest() {
        val sendReq = SearchManagerService().searchTopList()
        val response: ArrayList<String> = sendReq
        this.queryList = response
    }

}