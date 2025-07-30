package tj.paykar.shop.domain.usecase.shop

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.repository.shop.AddPointsRegistrationService
import tj.paykar.shop.data.service.RetrofitService

class AddPointsRegistrationManagerService:
    tj.paykar.shop.domain.reprository.shop.AddPointsRegistrationManager {
    override suspend fun addPointsToClient(
        firstName: String,
        lastName: String,
        typeOS: String,
        phoneNumber: String,
        cardCode: String,
        isQrCode: Boolean
    ) {
        val request = RetrofitService().serverRequest()
        val response = request.create(AddPointsRegistrationService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("firstName", firstName)
        jsonObject.put("lastName", lastName)
        jsonObject.put("typeOS", typeOS)
        jsonObject.put("phoneNumber", phoneNumber)
        jsonObject.put("cardCode", cardCode)
        jsonObject.put("isQrCode", isQrCode)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.addPointsToClient(requestBody)
    }
}