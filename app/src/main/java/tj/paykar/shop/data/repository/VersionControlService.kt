package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.VersionControlModel

interface VersionControlService {
    @POST("admin/func/checking_version.php")
    suspend fun checkVersion(@Body requestBody: RequestBody): Response<VersionControlModel?>
}