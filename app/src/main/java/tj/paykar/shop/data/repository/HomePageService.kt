package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tj.paykar.shop.data.model.home.HomeModel
import tj.paykar.shop.data.model.shop.ShopModel

interface HomePageService {
    @POST("bitrix/api/home/home_super_app.php")
    suspend fun getHomeData(@Body requestBody: RequestBody): Response<HomeModel>

    @GET("bitrix/api/home/home_shop.php")
    suspend fun getShopHomeData(@Query("userId") userId: Int): Response<ShopModel>
}