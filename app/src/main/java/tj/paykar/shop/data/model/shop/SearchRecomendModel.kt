package tj.paykar.shop.data.model.shop

import com.google.gson.annotations.SerializedName

data class SearchRecomendModel (
    val id: String,
    val name: String,
    var basket_quan: Double,
    val wishlist: Boolean,
    val detailText: String,
    val hit: String,
    @SerializedName("detail_picture")
    val picture: String,
    val price: String,
    val discountPrice: Double,
    val baseUnit: String,
    val nutritional: String?,
    val composition: String?,
    val termConditions: String?,
    val manufacturer: String?,
    val reviews: ArrayList<ProductReviewModel>?
)
