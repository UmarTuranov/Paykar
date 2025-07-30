package tj.paykar.shop.data.model

data class AuthorizationModel (
    val id: Int,
    val login: String?,
    val name: String?,
    val lastName: String?
)

data class CheckSMSModel(
    val code: String
)
