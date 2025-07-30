package tj.paykar.shop.data.repository.wallet

import okhttp3.RequestBody
import tj.paykar.shop.data.model.wallet.AccountOperationHistoryModel
import tj.paykar.shop.data.model.wallet.CheckOperationModel
import tj.paykar.shop.data.model.wallet.CheckQrOperationModel
import tj.paykar.shop.data.model.wallet.ConfirmCreateOperationModel
import tj.paykar.shop.data.model.wallet.CreateOperationModel
import tj.paykar.shop.data.model.wallet.CreateQrOperationModel
import tj.paykar.shop.data.model.wallet.OperationHistoryModel
import tj.paykar.shop.data.model.wallet.OperationInfoModel
import tj.paykar.shop.data.model.wallet.RequestAccountOperationHistoryModel
import tj.paykar.shop.data.model.wallet.RequestCheckOperationModel
import tj.paykar.shop.data.model.wallet.RequestConfirmCreateOperationModel
import tj.paykar.shop.data.model.wallet.RequestCreateOperationModel
import tj.paykar.shop.data.model.wallet.RequestOperationHistoryModel
import tj.paykar.shop.data.model.wallet.RequestOperationInfoModel
import tj.paykar.shop.data.model.wallet.RequestCreateQrOperationModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OperationService {
    @POST("api/operation/history")
    suspend fun getOperationHistory(@Body requestBody: RequestOperationHistoryModel, @Header("Sign") sign: String): Response<OperationHistoryModel>

    @POST("api/operation/info")
    suspend fun getOperationInfo(@Body requestBody: RequestOperationInfoModel, @Header("Sign") sign: String): Response<OperationInfoModel>

    @POST("api/operation/history/account")
    suspend fun getAccountOperationHistory(@Body requestBody: RequestAccountOperationHistoryModel, @Header("Sign") sign: String): Response<AccountOperationHistoryModel>

    @POST("api/operation/check")
    suspend fun checkOperation(@Body requestBody: RequestCheckOperationModel, @Header("Sign") sign: String): Response<CheckOperationModel>

    @POST("api/operation/create")
    suspend fun createOperation(@Body requestBody: RequestCreateOperationModel, @Header("Sign") sign: String): Response<CreateOperationModel>

    @POST("api/operation/create/confirm")
    suspend fun confirmCreateOperation(@Body requestBody: RequestConfirmCreateOperationModel, @Header("Sign") sign: String): Response<ConfirmCreateOperationModel>

    @POST("api/operation/qr/check")
    suspend fun checkQrOperation(@Body requestBody: RequestBody, @Header("Sign") sign: String): Response<CheckQrOperationModel>

    @POST("api/operation/qr/create")
    suspend fun createQrOperation(@Body requestBody: RequestCreateQrOperationModel, @Header("Sign") sign: String): Response<CreateQrOperationModel>
}