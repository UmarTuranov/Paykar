package tj.paykar.shop.domain.usecase

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.gift.DeferredGiftModel
import tj.paykar.shop.data.model.gift.PromotionModel
import tj.paykar.shop.data.model.gift.ReceiveGiftModel
import tj.paykar.shop.data.repository.GiftService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.GiftManager

class GiftManagerService: GiftManager {
    private val request = RetrofitService().paykarAdminRequest()
    private val response = request.create(GiftService::class.java)
    private val jsonObject = JSONObject()
    private val appJson = "application/json".toMediaTypeOrNull()

    override suspend fun checkGift(cardCode: String): Response<PromotionModel> {
        jsonObject.put("cardCode", cardCode)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.checkGift(requestBody)
    }

    override suspend fun receiveGift(
        phone: String,
        cardCode: String,
        unit: String,
        userId: Int,
        giftId: Int,
        fullName: String,
        categoryGift: String,
        sum: Double,
        numberCheck: String,
        type: String
    ): Response<ReceiveGiftModel> {
        jsonObject.put("phone", phone)
        jsonObject.put("cardCode", cardCode)
        jsonObject.put("unit", unit)
        jsonObject.put("userId", userId)
        jsonObject.put("giftId", giftId)
        jsonObject.put("fullName", fullName)
        jsonObject.put("categoryGift", categoryGift)
        jsonObject.put("sum", sum)
        jsonObject.put("numberCheck", numberCheck)
        jsonObject.put("type", type)
        val jsonObjectString = jsonObject.toString()
        Log.d("--RequestInfo", "RequestBody: $jsonObjectString")
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.receiveGift(requestBody)
    }

    override suspend fun deferGift(
        phone: String,
        cardCode: String,
        unit: String,
        userId: Int,
        giftId: Int,
        fullName: String,
        categoryGift: String,
        sum: Double,
        numberCheck: String
    ): Response<ReceiveGiftModel> {
        jsonObject.put("phone", phone)
        jsonObject.put("cardCode", cardCode)
        jsonObject.put("unit", unit)
        jsonObject.put("userId", userId)
        jsonObject.put("giftId", giftId)
        jsonObject.put("fullName", fullName)
        jsonObject.put("categoryGift", categoryGift)
        jsonObject.put("sum", sum)
        jsonObject.put("numberCheck", numberCheck)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.deferGift(requestBody)
    }

    override suspend fun deferredGiftList(cardCode: String): Response<DeferredGiftModel> {
        jsonObject.put("cardCode", cardCode)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.deferredGiftList(requestBody)
    }
}