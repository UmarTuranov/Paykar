package tj.paykar.shop.domain.usecase.wallet

import android.util.Log
import tj.paykar.shop.data.model.wallet.NotificationListModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.RequestNotificationModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.NotificationManager
import com.google.gson.Gson
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.repository.wallet.NotificationService

class NotificationManagerService: NotificationManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(NotificationService::class.java)
    private val gson = Gson()
    override suspend fun getNotificationList(
        customerId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<NotificationListModel> {
        val requestBody = RequestNotificationModel(customerId, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.getNotificationList(requestBody, hash)
    }
}