package tj.paykar.shop.data.model.shop

data class AddProductReviewModel(
    var productId: Int,
    var rating: Int,
    var userId: Int,
    var dignity: String,
    var flaws: String,
    var comments: String
)
