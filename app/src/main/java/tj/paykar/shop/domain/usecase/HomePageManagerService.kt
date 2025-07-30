package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.home.HomeModel
import tj.paykar.shop.data.model.shop.ShopModel
import tj.paykar.shop.data.repository.HomePageService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.HomePageManager

class HomePageManagerService: HomePageManager {
    private val request = RetrofitService().sendRequest()
    private val response = request.create(HomePageService::class.java)

    override suspend fun getHomeData(versionApp: String, userId: Int): Response<HomeModel> {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("versionApp", versionApp)
        jsonObject.put("typeOS", "Android")
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getHomeData(requestBody)
    }

    override suspend fun getShopData(userId: Int): Response<ShopModel> {
        return response.getShopHomeData(userId)
    }
}