package tj.paykar.shop.data.model.wallet

data class CreateOperationModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val OperId: Int?
)

data class RequestCreateOperationModel(
    val CustomerId: Int?,
    val CustomerAccount: String?,
    val ServiceId: Int?,
    val CheckId: Int?,
    val Account: String?,
    val Account2: String?,
    val Comment: String?,
    val Amount: Double?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

// CHECK OPERATION MODELS
data class CheckOperationModel(
    val CheckId: Int?,
    val Params: ArrayList<ParamsModel>,
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestCheckOperationModel(
    val CustomerId: Int?,
    val ServiceId: Int?,
    val Account: String?,
    val Account2: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class ParamsModel(
    val Name: String?,
    val Value: String?,
    val Title: String?
)
// CHECK OPERATION MODELS