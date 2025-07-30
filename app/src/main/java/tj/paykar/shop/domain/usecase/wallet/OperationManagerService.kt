package tj.paykar.shop.domain.usecase.wallet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.data.model.wallet.AccountOperationHistoryModel
import tj.paykar.shop.data.model.wallet.CheckOperationModel
import tj.paykar.shop.data.model.wallet.CheckQrOperationModel
import tj.paykar.shop.data.model.wallet.ConfirmCreateOperationModel
import tj.paykar.shop.data.model.wallet.CreateOperationModel
import tj.paykar.shop.data.model.wallet.CreateQrOperationModel
import tj.paykar.shop.data.model.wallet.OperationHistoryModel
import tj.paykar.shop.data.model.wallet.OperationInfoModel
import tj.paykar.shop.data.model.wallet.RequestAccountOperationHistoryModel
import tj.paykar.shop.data.model.wallet.RequestCheckOperationModel
import tj.paykar.shop.data.model.wallet.RequestConfirmCreateOperationModel
import tj.paykar.shop.data.model.wallet.RequestCreateOperationModel
import tj.paykar.shop.data.model.wallet.RequestCreateQrOperationModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.RequestOperationHistoryModel
import tj.paykar.shop.data.model.wallet.RequestOperationInfoModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.OperationManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.repository.wallet.OperationService

class OperationManagerService: OperationManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(OperationService::class.java)
    private val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getOperationHistory(
        customerId: Int,
        startDate: String,
        endDate: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<OperationHistoryModel> {
        val requestBody = RequestOperationHistoryModel(customerId, startDate, endDate, deviceInfo, requestInfo)
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
        return response.getOperationHistory(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getOperationInfo(
        customerId: Int,
        operationId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<OperationInfoModel> {
        val requestBody = RequestOperationInfoModel(customerId, operationId, deviceInfo, requestInfo)
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
        return response.getOperationInfo(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAccountOperationHistory(
        customerId: Int,
        customerAccount: String,
        startDate: String,
        endDate: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<AccountOperationHistoryModel> {
        val requestBody = RequestAccountOperationHistoryModel(customerId, customerAccount, startDate, endDate, deviceInfo, requestInfo)
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
        return response.getAccountOperationHistory(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun checkOperation(
        customerId: Int,
        serviceId: Int,
        account: String,
        account2: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<CheckOperationModel> {
        val requestBody = RequestCheckOperationModel(customerId, serviceId, account, account2, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.checkOperation(requestBody, hash)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createOperation(
        customerId: Int,
        customerAccount: String,
        serviceId: Int,
        checkId: Int,
        account: String,
        account2: String,
        comment: String,
        amount: Double,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<CreateOperationModel> {
        val requestBody = RequestCreateOperationModel(customerId, customerAccount, serviceId, checkId, account, account2, comment, amount, deviceInfo, requestInfo)
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
        return response.createOperation(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun confirmCreateOperation(
        customerId: Int,
        checkId: Int,
        confirmCode: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<ConfirmCreateOperationModel> {
        val requestBody = RequestConfirmCreateOperationModel(customerId, checkId, confirmCode, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.confirmCreateOperation(requestBody, hash)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun checkQrOperation(
        customerId: Int,
        account: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<CheckQrOperationModel> {
        val gson = GsonBuilder()
            .disableHtmlEscaping() // Убирает \/ экранирование
            .create()


        val jsonObject = JsonObject()
        jsonObject.addProperty("CustomerId", customerId)
        jsonObject.addProperty("Account", account)

        val deviceInfoJson = JsonObject()
        deviceInfoJson.addProperty("AppVersion", deviceInfo.AppVersion)
        deviceInfoJson.addProperty("Token", deviceInfo.Token)
        deviceInfoJson.addProperty("Imei", deviceInfo.Imei)
        jsonObject.add("deviceInfo", deviceInfoJson)

        val requestInfoJson = JsonObject()
        requestInfoJson.addProperty("Country", requestInfo.Country)
        requestInfoJson.addProperty("Host", requestInfo.Host)
        requestInfoJson.addProperty("Lang", requestInfo.Lang)
        jsonObject.add("requestInfo", requestInfoJson)
        val requestBodyJson = gson.toJson(jsonObject)


        //val requestBodyJson = jsonObject.toString()
        //val requestBody = RequestCheckQrOperationModel(customerId, account, deviceInfo, requestInfo)
       // val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val requestBody = requestBodyJson.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkQrOperation(requestBody, hash)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createQrOperation(
        customerId: Int,
        customerAccount: String,
        checkId: Int,
        comment: String,
        amount: Double,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<CreateQrOperationModel> {
        val requestBody = RequestCreateQrOperationModel(customerId, customerAccount, checkId, comment, amount, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.createQrOperation(requestBody, hash)
    }
}