package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.AddProductReviewModel

interface AddProductReviewManager {
    suspend fun addProductReview(reviewModel: AddProductReviewModel)
}