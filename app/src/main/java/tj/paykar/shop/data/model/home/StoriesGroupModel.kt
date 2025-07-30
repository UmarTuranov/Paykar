package tj.paykar.shop.data.model.home

data class StoriesGroupModel(
    val groupId: Int,
    val groupName: String,
    val groupPicture: String,
    val groupItemList: ArrayList<StoriesModel>
)
