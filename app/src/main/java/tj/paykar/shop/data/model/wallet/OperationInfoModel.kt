package tj.paykar.shop.data.model.wallet

data class OperationInfoModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val OperationInfo: Operation
)

data class RequestOperationInfoModel(
    val CustomerId: Int?,
    val OperId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
