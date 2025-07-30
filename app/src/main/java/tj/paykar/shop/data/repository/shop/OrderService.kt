package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.CheckSMSModel

interface OrderService {
    @GET("smsapi/index.php")
    suspend fun sendSms( @Query("MobilePhone") phone: String): CheckSMSModel
}