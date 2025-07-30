package tj.paykar.shop.data.model.id

data class ConfirmLoginModel(
    val status: String?,
    val message: String?,
    val userInfo: PaykarIdUserInfoModel?,
    val loyalty: ConfirmLoginLoyaltyModel?,
    val shop: UserShopModel?,
    val wallet: WalletModel?
)

data class ConfirmLoginLoyaltyModel(
    val ApiAccessTokenId: Int?,
    val Client: LoyaltyClientModel?,
    val Token: String?,
    val EpirationDate: String?,
    val Message: String?,
    val ClientGifts: String?,
    val DeviceName: String?,
    val DeviceVersion: String?,
    val LastLoginTime: String?
)

data class LoyaltyClientModel(
    val AccountId: Int?,
    val FullName: String?,
    val Gender: String?,
    val Birthday: String?,
    val PhoneMobile: String?,
    val Email: String?,
    val Balance: Double?,
    val Cards: List<LoyaltyCardModel>?,
    val ClientPromoCode: String?,
    val Turnover: Double?,
    val ClientChipInfo: Any?
)

data class LoyaltyCardModel(
    val CardId: Int?,
    val AccountId: Int?,
    val CardCode: String?,
    val Blocked: Boolean?,
    val BlockedString: String?,
    val AccumulateOnly: Boolean?,
    val AccumulateOnlyString: String?,
    val CardStatusId: Int?,
    val StartDate: String?,
    val FinishDate: String?,
    val BonusProgramName: String?,
    val Algorithm: String?,
    val BonusProgramId: Int?,
    val BonusProgramTypeId: Int?,
    val Balance: Double?,
    val Discount: Double?,
    val Turnover: Double?,
    val HasSales: Boolean?,
    val IsSuspicious: Boolean?,
    val CreateByMarketProgramId: Any?, // если структура известна, замените на конкретный тип
    val CardType: String?,
    val AccountTypeName: String?,
    val IsPromoCard: Boolean?,
    val WalletBarCodeFormatId: Any?, // если структура известна, замените на конкретный тип
    val AccountTypeId: Int?
)


data class WalletModel(
    val CustomerId: Int?,
    val isRegistration: Boolean?,
    val Token: String?
)
