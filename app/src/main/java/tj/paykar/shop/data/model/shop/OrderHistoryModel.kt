package tj.paykar.shop.data.model.shop

data class OrderHistoryModel (
    val id: String,
    val date: String,
    val payed: String,
    val deducted: String,
    val status_id: String,
    val date_status: String,
    val pirce_delivery: String,
    val pirce: String,
    val discount_value: String,
    val sum_paid: String,
    val comments: String,
    val canceled: String,
    val date_canceled: String,
    val reason_canceled: String,
    val user_name: String,
    val user_lastname: String,
    val basket_order_count: String,
    val basket_order: ArrayList<ProductOrder>
)

data class ProductOrder (
    val PRODUCT_ID: String,
    val NAME: String,
    val PRICE: String,
    val DISCOUNT_PRICE: String,
    val QUANTITY: String
)