package tj.paykar.shop.presentation.card.history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
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
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityCardHistoryBinding
import tj.paykar.shop.databinding.ActivityVirtualCardBinding
import tj.paykar.shop.domain.adapter.card.HistoryCardAdapter

class CardHistoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityCardHistoryBinding
    private val adapter = HistoryCardAdapter(this)

    private var userInfo: UserCardStorageModel = UserCardStorageModel(0, "false","","","", "", "", "", "", "", "", "", "", "", false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardHistoryBinding.inflate(layoutInflater)
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
        setupAdpter()
        getUser()

        if (userInfo.authorized == "true") {
            getHistory()
            service()
        }

    }

    private fun setupAdpter() {
        binding.apply {
            history.setHasFixedSize(true)
            history.layoutManager = LinearLayoutManager(this@CardHistoryActivity, LinearLayoutManager.VERTICAL, false)
            history.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        Log.d("--T Token", userInfo.token ?: "")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                adapter.sendRequest(userInfo.token ?: "")
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@CardHistoryActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@CardHistoryActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getHistory() {
        val historyList = UserStorageData(this).getCardHistory()
        if (historyList != null) {
            adapter.updateHistory(historyList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getUser() {
        userInfo = UserStorageData(this).getInfoCard()
    }

}