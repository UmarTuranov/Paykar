package tj.paykar.shop.data.repository.id

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.id.AutoRegistrationModel
import tj.paykar.shop.data.model.id.CheckLoginModel
import tj.paykar.shop.data.model.id.ConfirmLoginModel

interface PaykarIdService {
    @POST("/api/login/check.php")
    suspend fun checkLogin(@Body requestBody: RequestBody): Response<CheckLoginModel>

    @POST("/api/login/confirm.php")
    suspend fun confirmLogin(@Body requestBody: RequestBody): Response<ConfirmLoginModel>

    @POST("/api/user/registration.php")
    suspend fun autoRegistration(@Body requestBody: RequestBody): Response<AutoRegistrationModel>

    @POST("/api/user/update.php")
    suspend fun updateUser(@Body requestBody: RequestBody): Response<JsonObject>
}