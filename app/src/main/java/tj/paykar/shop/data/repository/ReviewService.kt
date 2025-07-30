package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ReviewService {
    @POST("admin/func/save_feedback.php")
    suspend fun sendReview(@Body requestBody: RequestBody)
}