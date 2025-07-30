package tj.paykar.shop.domain.reprository

import tj.paykar.shop.data.model.PaykarReviewModel

interface ReviewManager {
    suspend fun sendReview(review: PaykarReviewModel)
}