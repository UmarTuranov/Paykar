package tj.paykar.shop.domain.usecase.wallet

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.RequestResetFirebaseTokenModel
import tj.paykar.shop.data.model.wallet.ResetFirebaseTokenModel
import tj.paykar.shop.data.repository.wallet.ResetFirebaseTokenService
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.ResetFirebaseTokenManager

class ResetFirebaseTokenManagerService: ResetFirebaseTokenManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(ResetFirebaseTokenService::class.java)
    private val gson = Gson()
    override suspend fun resetFtoken(
        customerId: Int,
        ftoken: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<ResetFirebaseTokenModel> {
        val requestBody = RequestResetFirebaseTokenModel(customerId, ftoken, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.resetFtoken(requestBody, hash)
    }
}