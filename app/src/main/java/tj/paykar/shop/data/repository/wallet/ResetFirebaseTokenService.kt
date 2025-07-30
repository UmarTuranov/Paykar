package tj.paykar.shop.data.repository.wallet

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import tj.paykar.shop.data.model.wallet.RequestResetFirebaseTokenModel
import tj.paykar.shop.data.model.wallet.ResetFirebaseTokenModel

interface ResetFirebaseTokenService {
    @POST("api/customer/firebasetoken/reset")
    suspend fun resetFtoken(@Body requestBody: RequestResetFirebaseTokenModel, @Header("Sign") sign: String): Response<ResetFirebaseTokenModel>
}