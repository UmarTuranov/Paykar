package tj.paykar.shop.data.repository.shop

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.OrderHistoryModel

interface OrderHistoryService {

    @POST("bitrix/api/order/order.php")
    suspend fun createOrder(@Body requestBody: RequestBody): Response<Int>

    @GET("bitrix/api/order/getOrderList.php")
    suspend fun getOrderlist( @Query("userID") userId: Int): ArrayList<OrderHistoryModel>

    @GET("bitrix/api/order/repeatOrder.php")
    suspend fun repeatOrder( @Query("userId") userId: Int, @Query("orderId") orderId: Int)

    @GET("bitrix/api/order/cancellation.php")
    suspend fun cancellationOrder(@Query("orderId") orderId: Int)

}