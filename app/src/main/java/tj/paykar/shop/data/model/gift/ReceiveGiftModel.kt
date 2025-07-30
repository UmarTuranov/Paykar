package tj.paykar.shop.data.model.gift

data class ReceiveGiftModel(
    val status: String?,
    val message: String?,
    val deferred: ArrayList<DeferredGiftListModel>?
)
