package tj.paykar.shop.domain.usecase.id

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.id.AutoRegistrationModel
import tj.paykar.shop.data.model.id.CheckLoginModel
import tj.paykar.shop.data.model.id.ConfirmLoginModel
import tj.paykar.shop.data.model.id.UpdateUserModel
import tj.paykar.shop.data.repository.id.PaykarIdService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.id.PaykarIdManager

class PaykarIdManagerService: PaykarIdManager {
    private val request = RetrofitService().paykarIdRequest()
    private val response = request.create(PaykarIdService::class.java)
    private val jsonObject = JSONObject()
    private val appJson = "application/json".toMediaTypeOrNull()
    override suspend fun checkLogin(phone: String): Response<CheckLoginModel> {
        jsonObject.put("phone", phone)
        val jsonObjectString = jsonObject.toString()
        Log.d("--RequestInfo", "CheckLogin RequestBody: $jsonObjectString")
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.checkLogin(requestBody)
    }

    override suspend fun confirmLogin(
        phone: String,
        firstName: String,
        lastName: String,
        deviceModel: String,
        typeOS: String,
        versionOS: String,
        versionApp: String,
        imei: String,
        macaddress: String,
        ipaddress: String,
        ftoken: String,
        confirmCode: String
    ): Response<ConfirmLoginModel> {
        jsonObject.put("phone", phone)
        jsonObject.put("firstName", firstName)
        jsonObject.put("lastName", lastName)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("typeOS", "Android")
        jsonObject.put("versionOS", versionOS)
        jsonObject.put("versionApp", versionApp)
        jsonObject.put("imei", imei)
        jsonObject.put("macaddress", "")
        jsonObject.put("ipaddress", ipaddress)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("confirmCode", confirmCode)
        val jsonObjectString = jsonObject.toString()
        Log.d("--RequestInfo", "ConfirmLogin RequestBody: $jsonObjectString")
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.confirmLogin(requestBody)
    }

    override suspend fun autoRegistration(
        phone: String,
        firstName: String,
        lastName: String,
        deviceModel: String,
        typeOS: String,
        versionOS: String,
        versionApp: String,
        imei: String,
        macAddress: String,
        ipAddress: String,
        ftoken: String,
        ltoken: String
    ): Response<AutoRegistrationModel> {
        jsonObject.put("phone", phone)
        jsonObject.put("firstName", firstName)
        jsonObject.put("lastName", lastName)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("typeOS", typeOS)
        jsonObject.put("versionOS", versionOS)
        jsonObject.put("versionApp", versionApp)
        jsonObject.put("imei", imei)
        jsonObject.put("macAddress", macAddress)
        jsonObject.put("ipAddress", ipAddress)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("ltoken", ltoken)
        val jsonObjectString = jsonObject.toString()
        Log.d("--RequestInfo", "AutoRegistration RequestBody: $jsonObjectString")
        val requestBody = jsonObjectString.toRequestBody(appJson)
        return response.autoRegistration(requestBody)
    }

    override suspend fun updateUser(
        ptoken: String,
        ftoken: String,
        ltoken: String,
        deviceModel: String,
        versionApp: String,
        versionOS: String,
        imei: String,
        macaddress: String,
        ipaddress: String
    ): UpdateUserModel? {
        jsonObject.put("ptoken", ptoken)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("ltoken", ltoken)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("versionApp", versionApp)
        jsonObject.put("versionOS", versionOS)
        jsonObject.put("imei", imei)
        jsonObject.put("macaddress", macaddress)
        jsonObject.put("ipaddress", ipaddress)
        val jsonObjectString = jsonObject.toString()
        Log.d("--RequestInfo", "UpdateUser RequestBody: $jsonObjectString")
        val requestBody = jsonObjectString.toRequestBody(appJson)
        val jsonResponse = response.updateUser(requestBody).body().toString()
        Log.d("--RequestInfo", "Response Body: $jsonResponse")
        val fixedJsonString =  jsonResponse.replace("\"Profile\":[]", "\"Profile\":null")
        val userInfo = Gson().fromJson(fixedJsonString, UpdateUserModel::class.java)
        return userInfo
    }
}