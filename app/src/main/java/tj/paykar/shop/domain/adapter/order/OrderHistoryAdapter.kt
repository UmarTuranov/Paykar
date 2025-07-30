package tj.paykar.shop.domain.adapter.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.OrderHistoryModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemOrderHistoryBinding
import tj.paykar.shop.domain.usecase.shop.OrderHistoryManagerService
import tj.paykar.shop.presentation.profile.history.OrderHistoryDetailActivity

class OrderHistoryAdapter(context: Context): RecyclerView.Adapter<OrderHistoryAdapter.OrderHolder>() {

    var orderList = ArrayList<OrderHistoryModel>()
    private var mContext = context
    private var userId: Int = 0

    inner class OrderHolder(view: View, orderList: ArrayList<OrderHistoryModel>, context: Context): RecyclerView.ViewHolder(view) {
        val biding = ItemOrderHistoryBinding.bind(view)

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(context: Context, order: OrderHistoryModel) = with(biding) {
            orderNumber.text = "Заказ №${order.id}"
            orderDate.text = order.date
            val status = order.status_id
            if (status == "N") {
                orderStatus.text = "Принят"
                orderStatus.setTextColor(ContextCompat.getColor(context,R.color.yellow))
                imageView.setColorFilter(context.getResources().getColor(R.color.yellow))}
            else if (order.canceled == "Y") {
                orderStatus.text = "Отменен"
                orderStatus.setTextColor(ContextCompat.getColor(context,R.color.red))
                imageView.setColorFilter(context.getResources().getColor(R.color.red))
                cancellation.isVisible = false
            }
            else if (status == "F") {
                orderStatus.text = "Выполнен"
                imageView.setColorFilter(context.getResources().getColor(R.color.green))
                cancellation.isVisible = false
            }
            else { orderStatus.text = "Ожидает обработки" }
            orderPrice.text = "${order.pirce} сомони"

            cancellation.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        cancellationOrder(order.id.toInt())
                        withContext(Dispatchers.Main) {
                            cancellation.isVisible = false
                            orderStatus.text = "Отменен"
                            orderStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                            imageView.setColorFilter(context.getResources().getColor(R.color.red))

                            val snack = Snackbar.make(root, "Заказ отменен!", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.red))
                            snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                            snack.setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                            //snack.setAction("Отменить") { }
                            snack.show()
                        }

                    } catch (e: Exception) {
                        val snack = Snackbar.make(root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(mContext, R.color.statusBarBackground))
                        snack.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                        snack.show()
                    }
                }
            }

        }

        init {

            view.setOnClickListener {
                val position: Int = adapterPosition

                UserStorageData(context).saveHistoryProduct(orderList[position].basket_order)
                val intent = Intent(view.context, OrderHistoryDetailActivity::class.java)
                intent.putExtra("orderNumber", orderList[position].id)
                intent.putExtra("orderDate", orderList[position].date)
                intent.putExtra("orderStatus", orderList[position].status_id)
                intent.putExtra("orderPrice", orderList[position].pirce)
                intent.putExtra("orderDeliveryPrice", orderList[position].pirce_delivery)
                view.context.startActivity(intent)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_order_history, parent, false)
        return OrderHolder(view, orderList, mContext)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        return holder.bind(mContext, orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    suspend fun sendRequest() {
        userId = UserStorageData(mContext).getUserId()
        val sendReq = OrderHistoryManagerService().orderHistoryItems(userId)//
        Log.d("Your OrderList", sendReq.toString())
        val response: ArrayList<OrderHistoryModel> = sendReq
        response.reverse()
        this.orderList = response
    }

    @SuppressLint("NotifyDataSetChanged")
    suspend fun cancellationOrder(id: Int) {
        OrderHistoryManagerService().cancellationOrder(id)
    }
}