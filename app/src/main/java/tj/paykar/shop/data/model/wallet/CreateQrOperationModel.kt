package tj.paykar.shop.data.model.wallet

data class CreateQrOperationModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val OperId: Int?
)

data class RequestCreateQrOperationModel(
    val CustomerId: Int?,
    val CustomerAccount: String?,
    val CheckId: Int?,
    val Comment: String?,
    val Amount: Double?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

// CHECK REQUEST QR PAYMENT
data class CheckQrOperationModel(
    val CheckId: Int?,
    val Params: ArrayList<ParamsModel>,
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestCheckQrOperationModel(
    val CustomerId: Int?,
    val Account: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
// CHECK REQUEST QR PAYMENT
