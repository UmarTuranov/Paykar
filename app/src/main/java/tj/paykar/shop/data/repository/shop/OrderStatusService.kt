package tj.paykar.shop.data.repository.shop

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.OrderStatusModel

interface OrderStatusService {
    @GET("bitrix/api/order/orderStatus.php")
    suspend fun getOrderStatus( @Query("userId") userId: Int): Response<OrderStatusModel>
}