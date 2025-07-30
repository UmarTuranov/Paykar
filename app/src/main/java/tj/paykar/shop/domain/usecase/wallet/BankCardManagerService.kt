package tj.paykar.shop.domain.usecase.wallet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.data.model.wallet.AddBankCardModel
import tj.paykar.shop.data.model.wallet.BankCardListModel
import tj.paykar.shop.data.model.wallet.DeleteBankCardModel
import tj.paykar.shop.data.model.wallet.RequestAddBankCardModel
import tj.paykar.shop.data.model.wallet.RequestBankCardListModel
import tj.paykar.shop.data.model.wallet.RequestDeleteBankCardModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.BankCardManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.model.wallet.BlockCardModel
import tj.paykar.shop.data.model.wallet.EditCardNameModel
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.RequestBlockCardModel
import tj.paykar.shop.data.model.wallet.RequestEditNameCardModel
import tj.paykar.shop.data.model.wallet.RequestResetCardPinCodeModel
import tj.paykar.shop.data.model.wallet.ResetCardPinCodeModel
import tj.paykar.shop.data.repository.wallet.BankCardService

class BankCardManagerService(): BankCardManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(BankCardService::class.java)
    private val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addBankCard(
        customerId: Int,
        cardType: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<AddBankCardModel> {
        val requestBody = RequestAddBankCardModel(customerId, cardType, deviceInfo, requestInfo)
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
        return response.addBankCard(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteBankCard(
        customerId: Int,
        cardId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<DeleteBankCardModel> {
        val requestBody = RequestDeleteBankCardModel(customerId, cardId, deviceInfo, requestInfo)
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
        return response.deleteBankCard(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun bankCardList(
        customerId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<BankCardListModel> {
        val requestBody = RequestBankCardListModel(customerId, deviceInfo, requestInfo)
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
        return response.bankCardList(requestBody, signHeaderValue)
    }

    override suspend fun getCardType(
        customerId: Int,
        cardNumber: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PaymentServiceInfoModel> {
        val jsonObject = JSONObject()
        jsonObject.put("CustomerId", customerId)
        jsonObject.put("CurdNumber", cardNumber)
        val deviceInfoJson = gson.toJson(deviceInfo)
        val requestInfoJson = gson.toJson(requestInfo)
        jsonObject.put("deviceInfo", deviceInfoJson)
        jsonObject.put("requestInfo", requestInfoJson)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(jsonObjectString)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        val signHeaderValue = hash
        return response.getCardType(requestBody, signHeaderValue)
    }

    override suspend fun editCardName(
        customerId: Int,
        cardId: Int,
        cardName: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<EditCardNameModel> {
        val requestBody = RequestEditNameCardModel(customerId, cardId, cardName, deviceInfo, requestInfo)
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
        return response.editCardName(requestBody, signHeaderValue)
    }

    override suspend fun blockCard(
        customerId: Int,
        cardId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<BlockCardModel> {
        val requestBody = RequestBlockCardModel(cardId, customerId, deviceInfo, requestInfo)
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
        return response.blockCard(requestBody, signHeaderValue)
    }

    override suspend fun resetPinCode(
        customerId: Int,
        cardId: Int,
        pin: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<ResetCardPinCodeModel> {
        val requestBody = RequestResetCardPinCodeModel(customerId, cardId, pin, deviceInfo, requestInfo)
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
        return response.resetCardPinCode(requestBody, signHeaderValue)
    }
}