package tj.paykar.shop.data.repository.wallet

import tj.paykar.shop.data.model.wallet.PinCodeModel
import tj.paykar.shop.data.model.wallet.RequestCheckPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestConfirmForgetPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestForgetPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestResetPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestSetPinCodeModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PinCodeService {
    @POST("api/customer/pin/set")
    suspend fun setPinCode(@Body requestBody: RequestSetPinCodeModel, @Header("Sign") sign: String): Response<PinCodeModel>

    @POST("api/customer/pin/reset")
    suspend fun resetPinCode(@Body requestBody: RequestResetPinCodeModel, @Header("Sign") sign: String): Response<PinCodeModel>

    @POST("api/customer/pin/check")
    suspend fun checkPinCode(@Body requestBody: RequestCheckPinCodeModel, @Header("Sign") sign: String): Response<PinCodeModel>

    @POST("api/customer/pin/forget")
    suspend fun forgetPinCode(@Body requestBody: RequestForgetPinCodeModel, @Header("Sign") sign: String): Response<PinCodeModel>

    @POST("api/customer/pin/code/confirm")
    suspend fun confirmForgetPinCode(@Body requestBody: RequestConfirmForgetPinCodeModel, @Header("Sign") sign: String): Response<PinCodeModel>
}