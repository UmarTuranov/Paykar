package tj.paykar.shop.domain.usecase

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.model.PaykarReviewModel
import tj.paykar.shop.data.repository.ReviewService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.ReviewManager

class ReviewManagerService: ReviewManager {
    override suspend fun sendReview(review: PaykarReviewModel) {
        val request = RetrofitService().serverRequest()
        val response = request.create(ReviewService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("fname", review.fname)
        jsonObject.put("phone", review.phone)
        jsonObject.put("card_number", review.card_number)
        jsonObject.put("address", review.address)
        jsonObject.put("topic", review.topic)
        jsonObject.put("message", review.message)
        jsonObject.put("fileName", review.file_name)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        return response.sendReview(requestBody)
    }
}