package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface UserMobileService {
    @POST("admin/func/create_user_mobile.php")
    suspend fun createMobileUser(@Body requestBody: RequestBody)
}