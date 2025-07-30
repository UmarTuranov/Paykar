package tj.paykar.shop.presentation.profile.history

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityOrderHistoryDetailBinding
import tj.paykar.shop.domain.adapter.order.OrderHistoryDetailAdapter
import tj.paykar.shop.domain.usecase.shop.OrderHistoryManagerService
import tj.paykar.shop.presentation.authorization.LoginActivity

class OrderHistoryDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderHistoryDetailBinding
    private val adapter = OrderHistoryDetailAdapter(this)

    var number: String = ""
    var date: String = ""
    var status: String = ""
    var price: String = ""
    var deliveryPrice: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = this.resources.getColor(R.color.greyActivityBackground)
        window.navigationBarColor = this.resources.getColor(R.color.greyActivityBackground)
        getPutIntent()
        setupAdapter()
        setupView()
        service()
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun setupView() {
        binding.apply {
            orderNumber.text = "Заказ №$number"
            orderDate.text = date
            if (status == "F") { status = "Выполнен" }
            else if (status == "N") {status = "Принят"}
            else if (status == "Y") {
                status = "Отменен"
                orderStatus.setTextColor(R.color.red)
            }
            else {status = "Ожидает обработки"}
            orderStatus.text = status
            orderPrice.text = "$price сомони"
            val totalPrice = price.toDouble() + deliveryPrice.toDouble()

            val userId = UserStorageData(this@OrderHistoryDetailActivity).getUserId()
            repeatHistory.setOnClickListener {
                val ptoken = PaykarIdStorage(this@OrderHistoryDetailActivity).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(this@OrderHistoryDetailActivity)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы повторить заказ необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(this@OrderHistoryDetailActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            OrderHistoryManagerService().repeatOrder(userId, number.toInt())
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@OrderHistoryDetailActivity, OrderHistoryActivity::class.java)
                                startActivity(intent)
                            }
                        } catch (e: Exception) {
                            val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(this@OrderHistoryDetailActivity, R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(this@OrderHistoryDetailActivity, R.color.white))
                            snack.show()
                        }
                    }
                }
            }
        }
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        number = bundle?.getString("orderNumber") ?: ""
        date = bundle?.getString("orderDate") ?: ""
        status = bundle?.getString("orderStatus") ?: ""
        price = bundle?.getString("orderPrice") ?: ""
        deliveryPrice = bundle?.getString("orderDeliveryPrice") ?: ""
    }

    private fun setupAdapter() {
        binding.apply {
            orderProduct.setHasFixedSize(true)
            orderProduct.layoutManager = LinearLayoutManager(this@OrderHistoryDetailActivity, LinearLayoutManager.VERTICAL, false)
            orderProduct.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        adapter.getProductList()
        adapter.notifyDataSetChanged()
    }
}