package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import tj.paykar.shop.data.model.loyalty.*

interface CardService {

    @POST("Mobile/StartLogin")
    suspend fun sendConfCode(@Query("phoneMobile") phone: String): Response<String>

    @POST("api/processing/SendConfirmCodeToClient")
    suspend fun sendSmsCode(@Body requestBody: RequestBody)

    @POST("api/processing/ConfirmClientPhone")
    suspend fun checkSmsCode(@Body requestBody: RequestBody) : Response<ResponseBody>

    @POST("SiteController/SetProfileInfo?LicenseGuid=E7AB2716-A73A-40DF-BD6F-56EEE7A505B0")
    suspend fun saveData(@Body requestBody: RequestBody): Response<InfoCardModel>

    @POST("Mobile/Login")
    suspend fun checkConfCode(@Body requestBody: RequestBody): Response<UserCardModel>

    @POST("bitrix/api/loyalty_history.php")
    suspend fun getHistory(@Body requestBody: RequestBody): ArrayList<HistoryCardModel>

    @GET("api/getsales.php")
    suspend fun getPromo(): ArrayList<PromoCardModel>

    @POST("api/processing/info")
    suspend fun getCardInfo(@Body requestBody: RequestBody): InfoCardModel

    @POST("api/processing/info")
    suspend fun getCardInfo2(@Body requestBody: RequestBody): Response<InfoCardModel>

    @POST("api/processing/info")
    suspend fun checkCard(@Body requestBody: RequestBody): Response<InfoCardModel>

    @POST("notification/func/writenotify.php")
    suspend fun writeNotify(@Body requestBody: RequestBody)

    @POST("usersapp/func/saveuser.php")
    suspend fun saveUser(@Body requestBody: RequestBody)

    @POST("MarketProgram/GetClientChipInfo")
    suspend fun getChip(@Query("token") token: String): ArrayList<ChipCardModel>

    @POST("Account/AccountInfo")
    suspend fun promoCodeList(@Query("Token") token: String): Response<UserCardModel>

    @POST("MarketProgram/GetActiveMarketProgramsMobileContent")
    suspend fun promoContentList(@Query("Token") token: String): Response<ArrayList<CuponContentModel>>
}