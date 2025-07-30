package tj.paykar.shop.data.model.wallet

data class BlockCardModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestBlockCardModel(
    val CardId: Int?,
    val CustomerId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
