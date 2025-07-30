package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.CarParkModel
import tj.paykar.shop.data.model.LastVisitModel
import tj.paykar.shop.data.repository.CarParkService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.CarParkManager

class CarParkManagerService: CarParkManager {
    override suspend fun checkCarNumber(carNumber: String): Response<CarParkModel?> {
        val request = RetrofitService().parkingRequest()
        val response = request.create(CarParkService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("plateNumber", carNumber)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkCarNumber(requestBody)
    }

    override suspend fun getLastVisit(userId: Int, plateNumber: String): Response<LastVisitModel> {
        val request = RetrofitService().parkingRequest()
        val response = request.create(CarParkService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("plateNumber", plateNumber)
        jsonObject.put("platform", "Android")
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getLastVisit(requestBody)
    }
}