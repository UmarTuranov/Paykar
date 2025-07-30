package tj.paykar.shop.data.model.shop

import com.google.gson.annotations.SerializedName

data class WishlistModel(
    val totalPrice: String?,
    val fullPrice: String?,
    val deliveryPrice: String?,
    @SerializedName("productItmes")
    val productList: ArrayList<ProductItems>
)

