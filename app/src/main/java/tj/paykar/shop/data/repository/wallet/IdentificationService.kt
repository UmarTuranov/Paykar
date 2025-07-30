package tj.paykar.shop.data.repository.wallet

import tj.paykar.shop.data.model.wallet.IdentificationModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IdentificationService {
    @POST("api/customer/identity")
    suspend fun identify(@Body requestBody: RequestBody,
                         @Header("Sign") sign: String
    ): Response<IdentificationModel>
}