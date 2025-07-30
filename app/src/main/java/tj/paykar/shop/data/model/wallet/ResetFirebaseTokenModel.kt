package tj.paykar.shop.data.model.wallet

data class ResetFirebaseTokenModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestResetFirebaseTokenModel(
    val CustomerId: Int?,
    val FirebaseToken: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
