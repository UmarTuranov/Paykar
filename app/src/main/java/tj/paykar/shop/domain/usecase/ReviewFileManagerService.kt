package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import tj.paykar.shop.data.model.FileNameModel
import tj.paykar.shop.data.repository.ReviewFileService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.ReviewFileManager
import java.io.File

class ReviewFileManagerService: ReviewFileManager {

    override suspend fun uploadFile(file: File): Response<FileNameModel?> {
        val request = RetrofitService().serverRequest()
        val response = request.create(ReviewFileService::class.java)
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("media_production", file.name, requestBody)
        return response.uploadFile(filePart)
    }
}