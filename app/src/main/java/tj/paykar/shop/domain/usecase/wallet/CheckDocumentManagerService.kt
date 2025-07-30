package tj.paykar.shop.domain.usecase.wallet

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.model.wallet.CheckDocumentModel
import tj.paykar.shop.data.model.wallet.RequestCheckDocumentModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.repository.wallet.CheckDocumentService
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.CheckDocumentManager

class CheckDocumentManagerService: CheckDocumentManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(CheckDocumentService::class.java)
    private val gson = Gson()
    override suspend fun checkDocument(
        customerId: Int,
        taxId: String,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<CheckDocumentModel> {
        val requestBody = RequestCheckDocumentModel(customerId, taxId, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.checkDocument(requestBody, hash)
    }
}