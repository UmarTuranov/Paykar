package tj.paykar.shop.domain.usecase.wallet

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.wallet.CheckWalletModel
import tj.paykar.shop.data.model.wallet.ContactModel
import tj.paykar.shop.data.repository.wallet.CheckWalletService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.wallet.CheckWalletManager

class CheckWalletManagerService: CheckWalletManager {
    override suspend fun checkWallet(
        ptoken: String,
        contacts: ArrayList<ContactModel>
    ): Response<CheckWalletModel> {
        val request = RetrofitService().paykarIdRequest()
        val response = request.create(CheckWalletService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("ptoken", ptoken)
        val contactsJsonArray = JSONArray()
        for (i in contacts) {
            val contactsJsonObject = JSONObject()
            contactsJsonObject.put("phone", i.phone)
            contactsJsonObject.put("name", i.name)
            contactsJsonObject.put("email", i.email)
            contactsJsonObject.put("address", i.address)
            contactsJsonObject.put("organization", i.organization)
            contactsJsonArray.put(contactsJsonObject)
        }
        jsonObject.put("contacts", contactsJsonArray)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkWallet(requestBody)
    }
}