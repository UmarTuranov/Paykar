package tj.paykar.shop.data.model.shop

data class ProductLikeModel(
    val id: String,
    val name: String,
    var basket_quan: Double,
    val wishlist: Boolean,
    val hit: String,
    val base_unit: String,
    val nutritional: String?,
    val composition: String?,
    val termConditions: String?,
    val manufacturer: String?,
    val picture: String,
    val detail_picture: String,
    val price: String,
    val detail_text: String,
    val discountPrice: String,
    val discount: String,
    val reviews: ArrayList<ProductReviewModel>?
)