package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.model.shop.NotifycationModel
import tj.paykar.shop.data.model.loyalty.NotifycationCardModel
import tj.paykar.shop.data.repository.shop.NotifycationService
import tj.paykar.shop.data.service.RetrofitService

class NotifyManagerService: tj.paykar.shop.domain.reprository.shop.NotifycationManager {

    private val retrofitService = RetrofitService()

    override suspend fun notifyList(phone: String, token: String): ArrayList<NotifycationModel> {
        val request = retrofitService.serverRequest()
        val response = request.create(NotifycationService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("typeos", "Android")
        jsonObject.put("phone", phone)
        jsonObject.put("token", token)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getNotifyShop(requestBody)
    }

    override suspend fun notifyCardList(
        phone: String,
        token: String
    ): ArrayList<NotifycationCardModel> {
        val request = retrofitService.serverRequest()
        val response = request.create(NotifycationService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("typeos", "Android")
        jsonObject.put("phone", phone)
        jsonObject.put("token", token)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getNotifyCard(requestBody)
    }

}