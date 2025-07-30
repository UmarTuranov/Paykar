package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tj.paykar.shop.data.model.*

interface AuthorizationService {

    @GET("bitrix/api/sms/send.php")
    suspend fun sendSms( @Query("MobilePhone") phone: String): CheckSMSModel

    @GET("bitrix/api/login.php")
    suspend fun login( @Query("login") phone: String): AuthorizationModel

    @POST("bitrix/api/registration.php")
    suspend fun registration(@Body requestBody: RequestBody): Response<RegistrationModel>

    @POST("bitrix/api/users/updateUser.php")
    suspend fun updateUser(@Body requestBody: RequestBody): Response<UserUpdateResultModel>
}