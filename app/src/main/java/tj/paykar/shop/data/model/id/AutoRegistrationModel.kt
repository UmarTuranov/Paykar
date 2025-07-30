package tj.paykar.shop.data.model.id

data class AutoRegistrationModel(
    val status: String?,
    val message: String?,
    val userInfo: PaykarIdUserInfoModel?,
    val loyalty: LoyaltyModel?,
    val shop: UserShopModel?,
    val wallet: WalletModel?
)
