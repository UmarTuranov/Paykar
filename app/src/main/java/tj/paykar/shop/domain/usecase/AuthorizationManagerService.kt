package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.*
import tj.paykar.shop.data.repository.AuthorizationService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.AuthorizationManager

class AuthorizationManagerService: AuthorizationManager {

    private val retrofitService = RetrofitService()

    override suspend fun sendSmsCode(phone: String): CheckSMSModel {
        val request = retrofitService.sendRequest()
        val response = request.create(AuthorizationService::class.java)
        return response.sendSms("992$phone")
    }

    override suspend fun login(phone: String): AuthorizationModel {
        val request = retrofitService.sendRequest()
        val response = request.create(AuthorizationService::class.java)
        return response.login("992$phone")
    }

    override suspend fun registration(firstName: String, lastName: String, phone: String): Response<RegistrationModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(AuthorizationService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("Fname", firstName)
        jsonObject.put("Lname", lastName)
        jsonObject.put("Login", "992$phone")
        jsonObject.put("Password", "iPayShop992$phone")
        jsonObject.put("ConfPassword", "iPayShop992$phone")
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.registration(requestBody)
    }

    override suspend fun updateUser(
        userUpdateModel: UserUpdateModel
    ): Response<UserUpdateResultModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(AuthorizationService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userUpdateModel.userId)
        jsonObject.put("firstName", userUpdateModel.firstName)
        jsonObject.put("lastName", userUpdateModel.lastName)
        jsonObject.put("secondName", userUpdateModel.secondName)
        jsonObject.put("phone", userUpdateModel.phone)
        jsonObject.put("birthday", userUpdateModel.birthday)
        jsonObject.put("gender", userUpdateModel.gender)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.updateUser(requestBody)
    }

}