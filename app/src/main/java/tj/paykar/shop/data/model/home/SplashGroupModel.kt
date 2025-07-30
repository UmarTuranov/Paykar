package tj.paykar.shop.data.model.home

data class SplashGroupModel(
    val categoryId: Int,
    val categoryName: String,
    val categoryPicture: String?,
    val categoryItemList: ArrayList<SplashModel>
)

data class SplashModel(
    val id: Int,
    val createDate: String,
    val title: String,
    val description: String,
    val picture: String,
    val background: String
)
