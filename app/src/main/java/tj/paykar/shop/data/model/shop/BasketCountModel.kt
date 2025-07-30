package tj.paykar.shop.data.model.shop

import com.google.gson.annotations.SerializedName

data class BasketCountModel(
    @SerializedName("basket_count")
    val count: Int
)
