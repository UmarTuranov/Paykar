package tj.paykar.shop.data.model.shop

import java.io.Serializable

data class ProductReviewModel(
    val userId: Int?,
    val userFullName: String?,
    val createDateTime: String?,
    val dignity: String?,
    val flaws: String?,
    val comments: String?,
    val rating: Int?
): Serializable
