package tj.paykar.shop.data.repository.wallet

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import tj.paykar.shop.data.model.wallet.CheckDocumentModel
import tj.paykar.shop.data.model.wallet.RequestCheckDocumentModel

interface CheckDocumentService {
    @POST("api/customer/document/check")
    suspend fun checkDocument(@Body requestBody: RequestCheckDocumentModel, @Header("Sign") sign: String): Response<CheckDocumentModel>
}