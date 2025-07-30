package tj.paykar.shop.domain.usecase.shop

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tj.paykar.shop.data.model.shop.AddProductReviewModel
import tj.paykar.shop.data.repository.shop.AddProductReviewService
import tj.paykar.shop.data.service.RetrofitService

class AddProductReviewManagerService:
    tj.paykar.shop.domain.reprository.shop.AddProductReviewManager {
    override suspend fun addProductReview(reviewModel: AddProductReviewModel) {
        val request = RetrofitService().sendRequest()
        val response = request.create(AddProductReviewService::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("productId", reviewModel.productId)
        jsonObject.put("rating", reviewModel.rating)
        jsonObject.put("userId", reviewModel.userId)
        jsonObject.put("dignity", reviewModel.dignity)
        jsonObject.put("flaws", reviewModel.flaws)
        jsonObject.put("comments", reviewModel.comments)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        response.addProductReview(requestBody)
    }
}