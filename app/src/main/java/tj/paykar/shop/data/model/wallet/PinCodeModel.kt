package tj.paykar.shop.data.model.wallet

data class PinCodeModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestSetPinCodeModel(
    val CustomerId: Int?,
    val PinCode: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class RequestResetPinCodeModel(
    val CustomerId: Int?,
    val OldPinCode: String?,
    val NewPinCode: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class RequestCheckPinCodeModel(
    val CustomerId: Int?,
    val PinCode: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class RequestForgetPinCodeModel (
    val CustomerId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class RequestConfirmForgetPinCodeModel (
    val CustomerId: Int?,
    val ConfirmCode: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)