package tj.paykar.shop.data.model.gift

data class GiftModel(
    val id: Int,
    val title: String?,
    val description: String?,
    val image: String?,
    val category_gift: String?
)

data class PromotionModel(
    val response: Boolean,
    val rule: String?,
    val gift: GiftModel?,
    val FirstName: String?,
    val LastName: String?,
    val SurName: String?,
    val CardCode: String?,
    val PhoneMobile: String?,
    val numberCheck: String?,
    val sum: Double?,
    val unit: String?,
)
