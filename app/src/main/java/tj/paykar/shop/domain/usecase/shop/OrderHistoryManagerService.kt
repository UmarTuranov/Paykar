package tj.paykar.shop.domain.usecase.shop

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.model.shop.OrderHistoryModel
import tj.paykar.shop.data.repository.shop.OrderHistoryService
import tj.paykar.shop.data.service.RetrofitService

class OrderHistoryManagerService: tj.paykar.shop.domain.reprository.shop.OrderHistoryManager {

    private val retrofitService = RetrofitService()

    override suspend fun createOrder(
        userId: Int,
        deliveryAdress: String,
        date: String,
        time: String,
        comment: String
    ): Int {
        val request = retrofitService.sendRequest()
        val response = request.create(OrderHistoryService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("userID", "$userId")
        jsonObject.put("adress", deliveryAdress)
        jsonObject.put("date", date)
        jsonObject.put("time", time)
        jsonObject.put("comment", comment)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.createOrder(requestBody).body() as Int
    }

    override suspend fun orderHistoryItems(userId: Int): ArrayList<OrderHistoryModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(OrderHistoryService::class.java)
        return response.getOrderlist(userId)
    }

    override suspend fun repeatOrder(userId: Int, orderId: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(OrderHistoryService::class.java)
        return response.repeatOrder(userId, orderId)
    }

    override suspend fun cancellationOrder(orderId: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(OrderHistoryService::class.java)
        response.cancellationOrder(orderId)
    }
}