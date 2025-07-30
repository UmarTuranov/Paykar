package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import tj.paykar.shop.data.model.VersionControlModel
import tj.paykar.shop.data.repository.VersionControlService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.VersionControlManager

class VersionControlManagerService: VersionControlManager {
    override suspend fun checkVersion(version: String): Response<VersionControlModel?> {
        val request = RetrofitService().serverRequest()
        val response = request.create(VersionControlService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("version_os", version)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.checkVersion(requestBody)
    }
}