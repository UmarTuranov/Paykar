package tj.paykar.shop.data.model.shop

import com.google.gson.annotations.SerializedName

data class BasketModel (
    val totalPrice: String?,
    val fullPrice: String?,
    val deliveryPrice: String?,
    @SerializedName("productItmes")
    val productList: ArrayList<ProductItems>
)

data class ProductItems (
    val id: String?,
    val name: String?,
    var basket_quan: String?,
    val detailText: String?,
    val hit: String?,
    val prewPicture: String,
    val picture: String?,
    val price: String?,
    val baseUnit: String?,
    val discountPrice: String?
)