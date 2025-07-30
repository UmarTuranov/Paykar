package tj.paykar.shop.data.model.id

import tj.paykar.shop.data.model.wallet.UserInfoModel

data class UpdateUserModel(
    val status: String?,
    val message: String?,
    val userInfo: PaykarIdUserInfoModel?,
    val loyalty: LoyaltyModel?,
    val wallet: UserInfoModel?
)
