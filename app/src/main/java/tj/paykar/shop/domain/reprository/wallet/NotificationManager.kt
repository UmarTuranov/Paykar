package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.NotificationListModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import retrofit2.Response

interface NotificationManager {
    suspend fun getNotificationList(customerId: Int,
                                    deviceInfo: RequestDeviceInfoModel,
                                    requestInfo: RequestInfoModel
    ): Response<NotificationListModel>

}