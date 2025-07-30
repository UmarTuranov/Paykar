package tj.paykar.shop.data.model.shop

data class RecommendationProductModel(
    val id: Int,
    val name: String?,
    val detailText: String?,
    val hit: String?,
    var basket_quan: Double?,
    val wishlist: Boolean,
    val detail_picture: String?,
    val price: String?,
    val baseUnit: String?,
    val nutritional: String?,
    val composition: String?,
    val termConditions: String?,
    val manufacturer: String?,
    val reviews: ArrayList<ProductReviewModel>?
)
