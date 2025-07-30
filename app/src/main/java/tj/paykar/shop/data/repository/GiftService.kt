package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.gift.DeferredGiftModel
import tj.paykar.shop.data.model.gift.PromotionModel
import tj.paykar.shop.data.model.gift.ReceiveGiftModel

interface GiftService {
    @POST("api/loyalty/new_year_promotion/getrandgift.php")
    suspend fun checkGift(@Body requestBody: RequestBody): Response<PromotionModel>

    @POST("api/loyalty/present/owner/create.php")
    suspend fun receiveGift(@Body requestBody: RequestBody): Response<ReceiveGiftModel>

    @POST("api/loyalty/present/deferred/create.php")
    suspend fun deferGift(@Body requestBody: RequestBody): Response<ReceiveGiftModel>

    @POST("/api/loyalty/present/deferred/list.php")
    suspend fun deferredGiftList(@Body requestBody: RequestBody): Response<DeferredGiftModel>
}