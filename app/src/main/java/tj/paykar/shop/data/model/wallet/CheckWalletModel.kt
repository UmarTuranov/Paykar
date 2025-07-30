package tj.paykar.shop.data.model.wallet

data class CheckWalletModel(
    val status: String?,
    val message: String?,
    val existingWallets: ArrayList<String>?
)

data class ContactModel(
    val phone: String?,
    val name: String?,
    val email: String?,
    val address: String?,
    val organization: String?
)