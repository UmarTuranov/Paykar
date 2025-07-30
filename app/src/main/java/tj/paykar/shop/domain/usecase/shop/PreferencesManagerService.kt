package tj.paykar.shop.domain.usecase.shop

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import tj.paykar.shop.data.model.shop.PreferencesModel
import tj.paykar.shop.data.repository.shop.PreferencesService
import tj.paykar.shop.data.service.RetrofitService

class PreferencesManagerService: tj.paykar.shop.domain.reprository.shop.PreferencesManager {
    override suspend fun getPreferencesList(): ArrayList<PreferencesModel> {
        val request = RetrofitService().serverRequest()
        val response = request.create(PreferencesService::class.java)
        return response.getPreferencesList()
    }

    override suspend fun savePreferences(userId: Int, preferences: ArrayList<PreferencesModel>) {
        val request = RetrofitService().serverRequest()
        val response = request.create(PreferencesService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        val jsonArray = JSONArray()
        for (preference in preferences) {
            if (preference.selected == true){
                val preferenceObject = JSONObject()
                preferenceObject.put("id", preference.id)
                jsonArray.put(preferenceObject)
            }
        }
        jsonObject.put("preferences", jsonArray)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.savePreferences(requestBody)
    }

    override suspend fun getUserPreferences(userId: Int): ArrayList<PreferencesModel> {
        val request = RetrofitService().serverRequest()
        val response = request.create(PreferencesService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.getUserPreferences(requestBody)
    }
}