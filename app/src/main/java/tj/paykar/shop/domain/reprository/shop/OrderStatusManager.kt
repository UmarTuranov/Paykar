package tj.paykar.shop.domain.reprository.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.OrderStatusModel

interface OrderStatusManager {
    suspend fun getOrderStatus(userId: Int): Response<OrderStatusModel>
}