package tj.paykar.shop.data.model.shop

data class OrderStatusModel(
    val orderId: Int?,
    val orderStatus: String?,
    val orderTotalPrice: Double?
)
