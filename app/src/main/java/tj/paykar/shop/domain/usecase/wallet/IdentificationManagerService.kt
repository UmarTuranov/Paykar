package tj.paykar.shop.domain.usecase.wallet

import android.content.Context
import android.util.Log
import tj.paykar.shop.data.model.wallet.IdentificationModel
import tj.paykar.shop.data.model.wallet.IdentificationPerson
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.service.WalletRetrofitService
import tj.paykar.shop.domain.reprository.wallet.IdentificationManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.WALLET_PSK
import tj.paykar.shop.data.repository.wallet.IdentificationService

class IdentificationManagerService(val context: Context): IdentificationManager {
    private val request = WalletRetrofitService().serverRequest()
    private val response = request.create(IdentificationService::class.java)
    override suspend fun identify(
        customerId: Int,
        personal: IdentificationPerson,
        deviceInfo: RequestDeviceInfoModel,
        requestInfo: RequestInfoModel
    ): Response<IdentificationModel> {
        val jsonObject = JSONObject()
        val personalDataJson = JSONObject()
        val deviceInfoJson = JSONObject()
        val requestInfoJson = JSONObject()
        jsonObject.put("CustomerId", customerId)
        /*
    "FirstName": "string",
    "LastName": "string",
    "FatherName": "string",
    "Gender": "string",
    "BirthDate": "2025-02-25T11:42:30.095Z",
    "BirthCountry": "string",
    "DocType": 0,
    "DocSeries": "string",
    "DocNumber": "string",
    "DocIssueDate": "2025-02-25T11:42:30.095Z",
    "DocExpDate": "2025-02-25T11:42:30.095Z",
    "DocIssuer": "string",
    "Country": "string",
    "City": "string",
    "Address": "string",
    "TaxId": "string",
    "Email": "string",
    "Passport1": "string",
    "Passport2": "string",
    "Selfi": "string"
         */
        personalDataJson.put("FirstName", "")
        personalDataJson.put("LastName", "")
        personalDataJson.put("FatherName", "")
        personalDataJson.put("Gender", "")
        personalDataJson.put("BirthCountry", "")
        personalDataJson.put("DocType", 0)
        personalDataJson.put("DocSeries", "")
        personalDataJson.put("DocNumber", "")
        personalDataJson.put("DocIssuer", "")
        personalDataJson.put("Country", "")
        personalDataJson.put("City", "")
        personalDataJson.put("Address", "")
        personalDataJson.put("TaxId", "")
        personalDataJson.put("Email", "")
        personalDataJson.put("Passport1", personal.Passport1)
        personalDataJson.put("Passport2", personal.Passport2)
        personalDataJson.put("Selfi", personal.Selfi)
        personalDataJson.put("BirthDate", "1900-01-01")
        personalDataJson.put("DocIssueDate", "1900-01-01")
        personalDataJson.put("DocExpDate", "1900-01-01")
        jsonObject.put("personal", personalDataJson)
        deviceInfoJson.put("AppVersion", deviceInfo.AppVersion)
        deviceInfoJson.put("Token", deviceInfo.Token)
        deviceInfoJson.put("Imei", deviceInfo.Imei)
        jsonObject.put("deviceInfo", deviceInfoJson)
        requestInfoJson.put("Country", requestInfo.Country)
        requestInfoJson.put("Host", requestInfo.Host)
        requestInfoJson.put("Lang", requestInfo.Lang)
        jsonObject.put("requestInfo", requestInfoJson)
        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("--RequestInfo", "requestBodyJson: $jsonString")
        val psk = WALLET_PSK
        var base64 = SecurityUtils().base64Encode(jsonString)
        Log.d("--RequestInfo", "MyBase64: $base64")
        base64 += psk
        Log.d("--RequestInfo", "MyBase64+psk: $base64")
        val hash = SecurityUtils().calculateSHA256(base64)
        println("hash: $hash")
        return response.identify(requestBody, hash)
    }
}