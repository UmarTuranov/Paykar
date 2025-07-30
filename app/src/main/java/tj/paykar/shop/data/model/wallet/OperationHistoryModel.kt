package tj.paykar.shop.data.model.wallet

data class OperationHistoryModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val Operations: ArrayList<Operation>
)

data class RequestOperationHistoryModel(
    val CustomerId: Int?,
    val StartDate: String?,
    val EndDate: String?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class Operation(
    val Id: Int?,
    val ServiceId: Int?,
    val ServiceName: String?,
    val Icon: String?,
    val Amount: Double?,
    val Comission: Double?,
    val TotalAmount: Double?,
    val State: String?,
    val Comment: String?,
    val OperationType: String?,
    val OperationDate: String?,
    val Recipient: String?,
    val Sender: String?,
    val ServiceCategoryName: String?,
    val PaymentMethod: String?
)