package tj.paykar.shop.data.repository.wallet

import tj.paykar.shop.data.model.wallet.PaymentCategoryModel
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.PaymentServiceModel
import tj.paykar.shop.data.model.wallet.RequestPaymentCategoryModel
import tj.paykar.shop.data.model.wallet.RequestPaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.RequestPaymentServiceModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentService {
    @POST("api/categories")
    suspend fun getPaymentCategory(@Body requestBody: RequestPaymentCategoryModel, @Header("Sign") sign: String): Response<PaymentCategoryModel>

    @POST("api/categories/services")
    suspend fun getPaymentService(@Body requestBody: RequestPaymentServiceModel, @Header("Sign") sign: String): Response<PaymentServiceModel>

    @POST("api/categories/services/id")
    suspend fun getPaymentServiceInfo(@Body requestBody: RequestPaymentServiceInfoModel, @Header("Sign") sign: String): Response<PaymentServiceInfoModel>
}