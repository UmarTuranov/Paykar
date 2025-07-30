package tj.paykar.shop.data.model.gift

data class DeferredGiftModel(
    val status: Boolean?,
    val gifts: ArrayList<DeferredGiftListModel>
)

data class DeferredGiftListModel(
    val deferred: DeferredModel,
    val giftDetails: GiftModel
)

data class DeferredModel(
    val id: Int?,
    val create_date: String?,
    val full_name: String?,
    val phone: String?,
    val status: String?,
    val card_code: String?,
    val unit: String?,
    val user_id: Int?,
    val gift_id: Int?,
    val category_gift: String?,
    val sum: Double?,
    val number_check: String?
)
