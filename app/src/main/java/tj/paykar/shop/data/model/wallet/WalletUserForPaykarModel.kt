package tj.paykar.shop.data.model.wallet

data class WalletUserForPaykarModel(
    val status: String?,
    val message: String?,
    val userData: WalletUserDataForPaykarModel?
)

data class WalletUserDataForPaykarModel(
    val id: Int?,
    val firstname: String?,
    val lastname: String?,
    val gender: String?,
    val phone_number: String?,
    val birth_date: String?,
    val balance: String?,
    val status: String?,
)
