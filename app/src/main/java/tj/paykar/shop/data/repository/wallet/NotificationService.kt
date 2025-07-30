package tj.paykar.shop.data.repository.wallet

import tj.paykar.shop.data.model.wallet.NotificationListModel
import tj.paykar.shop.data.model.wallet.RequestNotificationModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationService {
    @POST("api/notifications")
    suspend fun getNotificationList(@Body requestBody: RequestNotificationModel, @Header("Sign") sign: String): Response<NotificationListModel>
}