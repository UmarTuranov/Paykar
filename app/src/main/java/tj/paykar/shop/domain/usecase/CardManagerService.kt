package tj.paykar.shop.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.LICENSE_GUIRD
import tj.paykar.shop.data.model.loyalty.*
import tj.paykar.shop.data.repository.CardService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.CardManager
import java.time.LocalDateTime
import java.time.ZoneId

class CardManagerService: CardManager {

    private val retrofitService = RetrofitService()

    override suspend fun sendConfCode(phone: String): String {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val result = response.sendConfCode("+992$phone")
        return result.toString()
    }

    override suspend fun checkConfCode(phone: String, code: String): Response<UserCardModel> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("Login", "+992$phone")
        jsonObject.put("ConfirmCode", code)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkConfCode(requestBody)
    }

    override suspend fun sendSmsCode(phone: String, cardCode: String) {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("LicenseGuid", LICENSE_GUIRD)
        jsonObject.put("CardCode", cardCode)
        jsonObject.put("PhoneMobile", phone)
        jsonObject.put("SendByDefaultProvider", true)
        jsonObject.put("CommunicationTemplateId", 12)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.sendSmsCode(requestBody)
    }

    override suspend fun checkSmsCode(phone: String, cardCode: String, confCode: String): Boolean {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("LicenseGuid", LICENSE_GUIRD)
        jsonObject.put("CardCode", cardCode)
        jsonObject.put("PhoneMobile", phone)
        jsonObject.put("ConfirmCode", confCode)
        jsonObject.put("SendByDefaultProvider", true)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val result = response.checkSmsCode(requestBody)
        return result.isSuccessful
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveCardInfo(
        clientId: Int,
        lastName: String,
        firstName: String,
        secondName: String,
        birthdate: String,
        phone: String,
        email: String,
        city: String,
        street: String,
        house: String,
        genderId: Int,
        phoneEnable: Boolean
    ): Response<InfoCardModel> {

        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()

        jsonObject.put("ClientId", clientId)
        if (phoneEnable){
            jsonObject.put("PhoneMobile", phone)
        }

        if (lastName != "") { jsonObject.put("LastName", lastName) }
        if (firstName != "") { jsonObject.put("FirstName", firstName) }
        if (secondName != "") { jsonObject.put("SurName", secondName) }
        if (birthdate != ""){ jsonObject.put("Birthday", birthdate)}
        if (email != "") { jsonObject.put("Email", email) }
        if (city != "") { jsonObject.put("City", city) }
        if (street != "") { jsonObject.put("Street", street) }
        if (house != "") { jsonObject.put("House", house) }
        if (genderId != 0) { jsonObject.put("GenderId", genderId) }

        jsonObject.put("AcceptSms", true)
        jsonObject.put("AccumulateOnly", false)

        val date = LocalDateTime.now()
        val instant = date.atZone(ZoneId.systemDefault()).toInstant()
        jsonObject.put("EditDate", instant.toString())
        val editor = JSONObject()
        editor.put("AccessUserId", 27)
        editor.put("FirstName", "Гулдаста")
        editor.put("LastName", "Каримова")
        jsonObject.put("Editor", editor)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.saveData(requestBody)

    }

    override suspend fun historyCardList(token: String): ArrayList<HistoryCardModel> {
        val request = retrofitService.paykarBitrixRequest()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("token", token)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getHistory(requestBody)
    }

    override suspend fun promoList(): ArrayList<PromoCardModel> {
        val request = retrofitService.siteRequest()
        val response = request.create(CardService::class.java)
        return response.getPromo()
    }

    override suspend fun cardInfo(phone: String, cardCode: String): InfoCardModel {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("LicenseGuid", "E7AB2716-A73A-40DF-BD6F-56EEE7A505B0")
        jsonObject.put("CardCode", cardCode)
        //jsonObject.put("MobilePhone", "+992$phone")
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getCardInfo(requestBody)
    }

    suspend fun cardInfo2(cardCode: String): Response<InfoCardModel> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("LicenseGuid", "E7AB2716-A73A-40DF-BD6F-56EEE7A505B0")
        jsonObject.put("CardCode", cardCode)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getCardInfo2(requestBody)
    }

    override suspend fun checkCardNumber(shortCardCode: String): Response<InfoCardModel> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("LicenseGuid", "E7AB2716-A73A-40DF-BD6F-56EEE7A505B0")
        jsonObject.put("ShortCardCode", shortCardCode)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkCard(requestBody)
    }

    override suspend fun cardCheck(phone: String): Response<InfoCardModel> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("LicenseGuid", "E7AB2716-A73A-40DF-BD6F-56EEE7A505B0")
        jsonObject.put("MobilePhone", "+992$phone")
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkCard(requestBody)
    }

    override suspend fun writeNotify(token: String, fToken: String, modelName: String, version: String) {
        val request = retrofitService.serverRequest()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("token", token)
        jsonObject.put("ftoken", fToken)
        jsonObject.put("modelName", modelName)
        jsonObject.put("version", version)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.writeNotify(requestBody)
    }

    override suspend fun saveUser(fname: String, birthday: String, adress: String, phone: String,
        email: String, cardcode: String, ftoken: String, deviceModel: String, typeOS: String, versionOS: String) {
        val request = retrofitService.serverRequest()
        val response = request.create(CardService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("fname", fname)
        jsonObject.put("birthday", birthday)
        jsonObject.put("adress", adress)
        jsonObject.put("phone", phone)
        jsonObject.put("email", email)
        jsonObject.put("cardcode", cardcode)
        jsonObject.put("ftoken", ftoken)
        jsonObject.put("deviceModel", deviceModel)
        jsonObject.put("typeOS", typeOS)
        jsonObject.put("versionOS", versionOS)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.saveUser(requestBody)
    }

    override suspend fun chipList(token: String): ArrayList<ChipCardModel> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        return response.getChip(token)
    }

    override suspend fun promoCode(token: String): Response<UserCardModel> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        return response.promoCodeList(token)
    }

    override suspend fun promoContent(token: String): Response<ArrayList<CuponContentModel>> {
        val request = retrofitService.processing()
        val response = request.create(CardService::class.java)
        return response.promoContentList(token)
    }
}