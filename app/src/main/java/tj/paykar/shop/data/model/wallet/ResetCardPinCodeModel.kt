package tj.paykar.shop.data.model.wallet

data class ResetCardPinCodeModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestResetCardPinCodeModel(
    val CustomerId: Int?,
    val CardId: Int?,
    val Pin: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
