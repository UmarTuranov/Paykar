package tj.paykar.shop.domain.reprository

import retrofit2.Response
import tj.paykar.shop.data.model.home.HomeModel
import tj.paykar.shop.data.model.shop.ShopModel

interface HomePageManager {
    suspend fun getHomeData(versionApp: String, userId: Int): Response<HomeModel>
    suspend fun getShopData(userId: Int): Response<ShopModel>
}