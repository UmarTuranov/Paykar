package tj.paykar.shop.data.repository.shop

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AddProductReviewService {
    @POST("bitrix/api/catalog/addProductReview.php")
    suspend fun addProductReview(@Body requestBody: RequestBody)
}