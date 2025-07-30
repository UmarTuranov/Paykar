package tj.paykar.shop.domain.usecase.wallet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.data.model.wallet.PaymentCategoryModel
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.PaymentServiceModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.RequestPaymentCategoryModel
import tj.paykar.shop.data.model.wallet.RequestPaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.RequestPaymentServiceModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.PaymentManager
import com.google.gson.Gson
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.repository.wallet.PaymentService

class PaymentManagerService: PaymentManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(PaymentService::class.java)
    private val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPaymentCategory(
        customerId: Int,
        categoryId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PaymentCategoryModel> {
        val requestBody = RequestPaymentCategoryModel(customerId, categoryId, deviceInfo, requestInfo)
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
        return response.getPaymentCategory(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPaymentService(
        customerId: Int,
        categoryId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PaymentServiceModel> {
        val requestBody = RequestPaymentServiceModel(customerId, categoryId, deviceInfo, requestInfo)
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
        return response.getPaymentService(requestBody, signHeaderValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPaymentServiceInfo(
        customerId: Int,
        serviceId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<PaymentServiceInfoModel> {
        val requestBody = RequestPaymentServiceInfoModel(customerId, serviceId, deviceInfo, requestInfo)
        val requestBodyJson = gson.toJson(requestBody)
        Log.d("--RequestInfo", "requestBodyJson: $requestBodyJson")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(requestBodyJson)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.getPaymentServiceInfo(requestBody, hash)
    }
}