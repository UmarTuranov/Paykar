package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.model.MyDevicesModel
import tj.paykar.shop.data.repository.MyDevicesService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.MyDevicesManager

class MyDevicesManagerService: MyDevicesManager {
    override suspend fun getDevices(phone: String): ArrayList<MyDevicesModel> {
        val request = RetrofitService().serverRequest()
        val response = request.create(MyDevicesService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("phone", phone)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getDevices(requestBody)
    }

}