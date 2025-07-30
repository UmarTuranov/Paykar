package tj.paykar.shop.data.model.wallet

data class IdentificationModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class IdentificationPerson(
    val Passport1: String?,
    val Passport2: String?,
    val Selfi: String?
)
