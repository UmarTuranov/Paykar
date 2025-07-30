package tj.paykar.shop.domain.usecase.wallet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.data.model.wallet.AuthorizationModel
import tj.paykar.shop.data.model.wallet.ConfirmUserModel
import tj.paykar.shop.data.model.wallet.RegistrationModel
import tj.paykar.shop.data.model.wallet.RequestAuthorizationModel
import tj.paykar.shop.data.model.wallet.RequestConfirmUserModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestDeviceTypeInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.RequestRegistrationModel
import tj.paykar.shop.data.model.wallet.RequestUserInfoModel
import tj.paykar.shop.data.model.wallet.UserInfoModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.UserManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.model.wallet.WalletUserForPaykarModel
import tj.paykar.shop.data.repository.wallet.UserService
import tj.paykar.shop.data.service.RetrofitService

class UserManagerService: UserManager {
    private val request = WalletRetrofitService().serverRequest()
    private val paykarRequest = RetrofitService().paykarAdminRequest()
    private val paykarResponse = paykarRequest.create(UserService::class.java)
    private val response = request.create(UserService::class.java)
    private val gson = Gson()
    private val jsonObject= JSONObject()

    override suspend fun createUser(
        login: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<RegistrationModel> {
        val requestBody = RequestRegistrationModel("992", login, deviceInfo, requestInfoModel)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val signHeaderValue = hash
        return response.createUser(requestBody, signHeaderValue)
    }

    override suspend fun login(
        login: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<AuthorizationModel> {
        val requestBody = RequestAuthorizationModel("992", login, deviceInfo, requestInfoModel)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val signHeaderValue = hash
        return response.login(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserInfo(
        customerId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<UserInfoModel> {
        val requestBody = RequestUserInfoModel(customerId, deviceInfo, requestInfoModel)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val signHeaderValue = hash
        return response.getUserInfo(requestBody, signHeaderValue)
    }

    override suspend fun confirmRegistrationUser(
        customerId: Int,
        confirmCode: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<ConfirmUserModel> {
        val requestBody = RequestConfirmUserModel(customerId, confirmCode, deviceInfo, requestInfoModel)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val signHeaderValue = hash
        return response.confirmRegistrationUser(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun confirmLoginUser(
        customerId: Int,
        confirmCode: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<ConfirmUserModel> {
        val requestBody = RequestConfirmUserModel(customerId, confirmCode, deviceInfo, requestInfoModel)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val signHeaderValue = hash
        return response.confirmLoginUser(requestBody, signHeaderValue)
    }

    override suspend fun createWalletUser(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        birthDate: String,
        deviceModel: String,
        versionOS: String,
        ftoken: String,
        imei: String,
        ipAddress: String,
        walletAppVersion: String,
        shopUserId: Int
    ): Response<WalletUserForPaykarModel> {
        jsonObject.put("firstName", firstName)
        jsonObject.put("lastName", lastName)
        jsonObject.put("phoneNumber", phoneNumber)
        jsonObject.put("birthDate", birthDate)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("versionOS", versionOS)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("imei", imei)
        jsonObject.put("ipAddress", ipAddress)
        jsonObject.put("shopUserId", shopUserId)
        jsonObject.put("typeOS", "Android")
        jsonObject.put("walletAppVersion", walletAppVersion)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return paykarResponse.createWalletUserPaykar(requestBody)
    }

    override suspend fun walletUserInfoPaykar(phone: String): Response<WalletUserForPaykarModel> {
        return paykarResponse.walletUserInfoPaykar(phone)
    }

    override suspend fun editWalletUserPaykar(
        walletUserId: Int,
        firstName: String,
        lastName: String,
        gender: String,
        phoneNumber: String,
        birthDate: String,
        balance: String,
        deviceModel: String,
        typeOS: String,
        versionOS: String,
        ftoken: String,
        walletAppVersion: String,
        imei: String,
        ipAddress: String,
        shopUserId: Int
    ): Response<WalletUserForPaykarModel> {
        jsonObject.put("walletUserId", walletUserId)
        jsonObject.put("firstName", firstName)
        jsonObject.put("lastName", lastName)
        jsonObject.put("gender", gender)
        jsonObject.put("phoneNumber", phoneNumber)
        jsonObject.put("birthDate", birthDate)
        jsonObject.put("balance", balance)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("typeOS", typeOS)
        jsonObject.put("versionOS", versionOS)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("walletAppVersion", walletAppVersion)
        jsonObject.put("imei", imei)
        jsonObject.put("ipAddress", ipAddress)
        jsonObject.put("shopUserId", shopUserId)
        val jsonObjectString = jsonObject.toString()
        Log.d("--RequestInfo", "RequestBody: $jsonObjectString")
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return paykarResponse.editWalletUserPaykar(requestBody)
    }

    override suspend fun updateWalletUserPaykar(
        walletUserId: Int,
        deviceModel: String,
        versionOS: String,
        ftoken: String,
        imei: String,
        ipAddress: String,
        walletAppVersion: String,
        balance: String
    ): Response<WalletUserForPaykarModel> {
        jsonObject.put("walletUserId", walletUserId)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("typeOS", "Android")
        jsonObject.put("versionOS", versionOS)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("walletAppVersion", walletAppVersion)
        jsonObject.put("imei", imei)
        jsonObject.put("ipAddress", ipAddress)
        jsonObject.put("balance", balance)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return paykarResponse.updateWalletUserPaykar(requestBody)
    }
}