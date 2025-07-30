package tj.paykar.shop.data.model.wallet

data class NotificationListModel(
    val ResultCode: Int?,
    val ResultDesc: String?,
    val Notifications: ArrayList<NotificationModel>
)

data class NotificationModel(
    val Name: String?,
    val Description: String?,
    val NotificationDate: String?
)

data class RequestNotificationModel(
    val CustomerId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
