package tj.paykar.shop.presentation.shop.search

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivitySearchResultBinding
import tj.paykar.shop.domain.adapter.search.SearchResultAdapter

class SearchResultActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchResultBinding
    private lateinit var search: androidx.appcompat.widget.SearchView
    private val searchResultAdapter = SearchResultAdapter(this)
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        binding.emptyResult.isVisible = false

        getPutIntent()
        setupAdapter()
    }

    override fun onResume() {
        super.onResume()
        service()
        setupSearch()
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        searchText = bundle?.getString("query") ?: ""
    }

    private fun setupAdapter() {
        binding.apply {

            searchResult.setHasFixedSize(true)
            searchResult.layoutManager = LinearLayoutManager(this@SearchResultActivity, LinearLayoutManager.VERTICAL, false)
            searchResult.adapter = searchResultAdapter

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                searchResultAdapter.sendRequest(searchText)
                withContext(Dispatchers.Main) {
                    searchResultAdapter.notifyDataSetChanged()
                    if(searchResultAdapter.productList.size == 0){
                        binding.emptyResult.isVisible = true
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this@SearchResultActivity, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this@SearchResultActivity, R.color.white))
                snack.show()
            }

        }
    }

    private fun setupSearch() {
        binding.apply {
            searchView.setQuery(searchText, false)
            search = searchView

            search.setOnQueryTextListener(object:  androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        searchText = query
                        searchResultAdapter.productList = ArrayList()
                        service()
                        search.clearFocus()
                        binding.emptyResult.isVisible = false
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return false
                }
            })

            search.setOnCloseListener(object :  androidx.appcompat.widget.SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    Toast.makeText(this@SearchResultActivity, "query", Toast.LENGTH_SHORT).show()
                    return true
                }
            })

            search.setOnSearchClickListener {
                Toast.makeText(this@SearchResultActivity, "click", Toast.LENGTH_SHORT).show()
            }
        }
    }

}