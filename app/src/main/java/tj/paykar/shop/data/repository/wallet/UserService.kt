package tj.paykar.shop.data.repository.wallet

import okhttp3.RequestBody
import tj.paykar.shop.data.model.wallet.AuthorizationModel
import tj.paykar.shop.data.model.wallet.ConfirmUserModel
import tj.paykar.shop.data.model.wallet.RegistrationModel
import tj.paykar.shop.data.model.wallet.RequestAuthorizationModel
import tj.paykar.shop.data.model.wallet.RequestConfirmUserModel
import tj.paykar.shop.data.model.wallet.RequestRegistrationModel
import tj.paykar.shop.data.model.wallet.RequestUserInfoModel
import tj.paykar.shop.data.model.wallet.UserInfoModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import tj.paykar.shop.data.model.wallet.WalletUserForPaykarModel

interface UserService {

    @POST("api/customer/auth")
    suspend fun login(@Body requestBody: RequestAuthorizationModel, @Header("Sign") sign: String): Response<AuthorizationModel>

    @POST("api/customer/info")
    suspend fun getUserInfo(@Body requestBody: RequestUserInfoModel, @Header("Sign") sign: String): Response<UserInfoModel>

    @POST("api/customer/create")
    suspend fun createUser(@Body requestBody: RequestRegistrationModel, @Header("Sign") sign: String): Response<RegistrationModel>

    @POST("api/customer/create/confirm")
    suspend fun confirmRegistrationUser(@Body requestBody: RequestConfirmUserModel, @Header("Sign") sign: String): Response<ConfirmUserModel>

    @POST("/api/customer/auth/confirm")
    suspend fun confirmLoginUser(@Body requestBody: RequestConfirmUserModel, @Header("Sign") sign: String): Response<ConfirmUserModel>

    @POST("/api/walletuser/create.php")
    suspend fun createWalletUserPaykar(@Body requestBody: RequestBody): Response<WalletUserForPaykarModel>

    @GET("/api/walletuser/info.php")
    suspend fun walletUserInfoPaykar(@Query("phone") phone: String): Response<WalletUserForPaykarModel>

    @POST("/api/walletuser/edit.php")
    suspend fun editWalletUserPaykar(@Body requestBody: RequestBody): Response<WalletUserForPaykarModel>

    @POST("/api/walletuser/update.php")
    suspend fun updateWalletUserPaykar(@Body requestBody: RequestBody): Response<WalletUserForPaykarModel>
}