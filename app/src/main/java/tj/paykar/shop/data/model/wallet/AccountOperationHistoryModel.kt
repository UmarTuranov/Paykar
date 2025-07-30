package tj.paykar.shop.data.model.wallet

data class AccountOperationHistoryModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val Operations: ArrayList<Operation>
)

data class RequestAccountOperationHistoryModel(
    val CustomerId: Int?,
    val CustomerAccount: String?,
    val StartDate: String?,
    val EndDate: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
