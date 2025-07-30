package tj.paykar.shop.presentation.shop.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivitySearchBinding
import tj.paykar.shop.domain.adapter.search.SearchRecomendAdapter
import tj.paykar.shop.domain.adapter.search.SearchTopAdapter
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.InternetConnectionActivity

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    private val topAdapter = SearchTopAdapter(this)
    private val recomendAdapter = SearchRecomendAdapter(this)
    private lateinit var search: androidx.appcompat.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.loadAnimation.isVisible = true
        checkInternet()
        searchSetup()
    }

    private fun visibleView(value: Boolean) {
        binding.apply {
            topSearchTitle.isVisible = value
            recommendedTitle.isVisible = value
        }
    }

    private fun checkInternet() {
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            startActivity(Intent(this, InternetConnectionActivity::class.java))
        } else {
            setupAdapter()
            service()
        }
    }

    private fun setupAdapter() {
        binding.apply {
            search = searchView
            topSearch.setHasFixedSize(true)
            topSearch.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            topSearch.adapter = topAdapter

            recommended.setHasFixedSize(true)
            recommended.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            //recommended.layoutManager = GridLayoutManager(this@SearchActivity, 2)
            recommended.adapter = recomendAdapter
        }
    }

    private fun searchSetup() {

        search.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }
        })

        search.setOnCloseListener(object : androidx.appcompat.widget.SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                Toast.makeText(this@SearchActivity, "query", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        search.setOnSearchClickListener {
            Toast.makeText(this@SearchActivity, "click", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        visibleView(false)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                topAdapter.sendRequest()
                recomendAdapter.sendRequest()
                withContext(Dispatchers.Main) {
                    visibleView(true)
                    binding.loadAnimation.isVisible = false
                    topAdapter.notifyDataSetChanged()
                    recomendAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this@SearchActivity, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this@SearchActivity, R.color.white))
                snack.show()
            }
        }
    }
}