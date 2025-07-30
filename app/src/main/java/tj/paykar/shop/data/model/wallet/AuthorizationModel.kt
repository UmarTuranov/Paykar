package tj.paykar.shop.data.model.wallet

data class AuthorizationModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val CustomerId: Int?,
)

data class RequestAuthorizationModel(
    val Code: String?,
    val Login: String?,
    val DeviceInfo: RequestDeviceTypeInfoModel,
    val RequestInfo: RequestInfoModel
)