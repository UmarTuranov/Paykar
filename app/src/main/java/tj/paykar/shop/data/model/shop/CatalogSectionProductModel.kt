package tj.paykar.shop.data.model.shop

data class CatalogSectionProductModel (

    val id: String,
    val name: String,
    var basket_quan: Double,
    val wishlist: Boolean,
    val detailText: String,
    val hit: String,
    val nutritional: String?,
    val composition: String?,
    val termConditions: String?,
    val manufacturer: String?,
    val prewPicture: String,
    val picture: String,
    val pictureDetail: String?,
    val price: String,
    val discountPrice: Double,
    val baseUnit: String,
    val detail_picture: String?,
    val base_unit: String?,
    val detail_text: String?,
    val reviews: ArrayList<ProductReviewModel>?
)