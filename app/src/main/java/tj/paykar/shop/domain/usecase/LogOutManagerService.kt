package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.repository.LogOutService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.LogOutManager

class LogOutManagerService: LogOutManager {
    override suspend fun logOut(phone: String, deviceModel: String) {
        val request = RetrofitService().serverRequest()
        val response = request.create(LogOutService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("phone", phone)
        jsonObject.put("deviceModel", deviceModel)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.logOut(requestBody)
    }

}