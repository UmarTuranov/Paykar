package tj.paykar.shop.data.model.wallet

data class ConfirmCreateOperationModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val OperId: Int?
)

data class RequestConfirmCreateOperationModel(
    val CustomerId: Int?,
    val CheckId: Int?,
    val ConfirmCode: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
