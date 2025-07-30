package tj.paykar.shop.presentation.shop.basket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityConfirmOrderBinding
import tj.paykar.shop.domain.usecase.shop.OrderHistoryManagerService
import java.time.LocalDate
import java.time.LocalTime

class ConfirmOrderActivity : AppCompatActivity() {

    lateinit var binding: ActivityConfirmOrderBinding
    private var userId = 0
    private var deliveryAdress = ""
    private var date = ""
    private var time = ""
    private var comment = ""
    private var getDay = ""
    private var getTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPutIntent()
        setupView()
        service()

    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        deliveryAdress = bundle?.getString("deliveryAddress") ?: ""
        comment = bundle?.getString("comment") ?: ""
        getDay = bundle?.getString("getDay") ?: ""
        getTime = bundle?.getString("getTime") ?: ""
        date = LocalDate.now().toString()
        time = LocalTime.now().toString()
        userId = UserStorageData(this).getUserId()
    }

    private fun service() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                OrderHistoryManagerService().createOrder(userId, deliveryAdress, getDay, getTime, comment)
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                withContext(Dispatchers.Main){
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@ConfirmOrderActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@ConfirmOrderActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    private fun setupView() {
        binding.apply {
            updatePage.setOnClickListener {
                val intent = Intent(this@ConfirmOrderActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}