package tj.paykar.shop.data.model.shop

data class NotificationGroupModel(
    val categoryId: Int,
    val categoryName: String,
    val categoryPicture: String?,
    val categoryItemList: ArrayList<NotifycationModel>
)
