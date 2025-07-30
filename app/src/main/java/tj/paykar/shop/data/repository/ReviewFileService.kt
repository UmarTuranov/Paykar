package tj.paykar.shop.data.repository

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import tj.paykar.shop.data.model.FileNameModel

interface ReviewFileService {
    @Multipart
    @POST("admin/func/upload_file.php")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<FileNameModel?>
}