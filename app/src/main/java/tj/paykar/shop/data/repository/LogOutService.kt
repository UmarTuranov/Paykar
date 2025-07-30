package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface LogOutService {
    @POST("api/devices/deleteUserDevice.php")
    suspend fun logOut(@Body requestBody: RequestBody)
}