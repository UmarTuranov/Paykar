package tj.paykar.shop.domain.usecase.wallet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.data.model.wallet.PinCodeModel
import tj.paykar.shop.data.model.wallet.RequestCheckPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestConfirmForgetPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestForgetPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.RequestResetPinCodeModel
import tj.paykar.shop.data.model.wallet.RequestSetPinCodeModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.PinCodeManager
import com.google.gson.Gson
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.repository.wallet.PinCodeService

class PinCodeManagerService: PinCodeManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(PinCodeService::class.java)
    private val gson = Gson()
    override suspend fun setPinCode(
        customerId: Int,
        pinCode: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PinCodeModel> {
        val requestBody = RequestSetPinCodeModel(customerId, pinCode, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.setPinCode(requestBody, hash)
    }

    override suspend fun resetPinCode(
        customerId: Int,
        oldPinCode: String,
        newPinCode: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PinCodeModel> {
        val requestBody = RequestResetPinCodeModel(customerId, oldPinCode, newPinCode, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.resetPinCode(requestBody, hash)
    }

    override suspend fun checkPinCode(
        customerId: Int,
        pinCode: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PinCodeModel> {
        val requestBody = RequestCheckPinCodeModel(customerId, pinCode, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.checkPinCode(requestBody, hash)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun forgetPinCode(
        customerId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PinCodeModel> {
        val requestBody = RequestForgetPinCodeModel(customerId, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.forgetPinCode(requestBody, hash)
    }

    override suspend fun confirmForgetPinCode(
        customerId: Int,
        confirmCode: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PinCodeModel> {
        val requestBody = RequestConfirmForgetPinCodeModel(customerId, confirmCode, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.confirmForgetPinCode(requestBody, hash)
    }
}