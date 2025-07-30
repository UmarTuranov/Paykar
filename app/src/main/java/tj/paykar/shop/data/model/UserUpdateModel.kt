package tj.paykar.shop.data.model

data class UserUpdateModel(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val secondName: String?,
    val phone: String,
    val birthday: String?,
    val gender: String?
)