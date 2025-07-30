package tj.paykar.shop.data.model

data class PaykarReviewModel(
    val fname: String,
    val phone: String,
    val card_number: String,
    val address: String,
    val topic: String,
    val message: String,
    var file_name: String?
)
