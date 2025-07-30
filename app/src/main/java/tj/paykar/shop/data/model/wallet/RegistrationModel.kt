package tj.paykar.shop.data.model.wallet

data class RegistrationModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val CustomerId: Int?
)

data class RequestRegistrationModel (
    val Code: String,
    val Login: String,
    val deviceInfo: RequestDeviceTypeInfoModel,
    val requestInfo: RequestInfoModel
)
