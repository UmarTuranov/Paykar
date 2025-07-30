package tj.paykar.shop.data.model.wallet

data class EditCardNameModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestEditNameCardModel(
    val CustomerId: Int?,
    val CardId: Int?,
    val CardName: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
