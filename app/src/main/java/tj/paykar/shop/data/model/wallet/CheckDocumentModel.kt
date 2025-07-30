package tj.paykar.shop.data.model.wallet

data class CheckDocumentModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestCheckDocumentModel(
    val CustomerId: Int?,
    val TaxId: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
