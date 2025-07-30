package tj.paykar.shop.data.repository.shop

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.shop.NotifycationModel
import tj.paykar.shop.data.model.loyalty.NotifycationCardModel

interface NotifycationService {

    @POST("notification/func/getnotifyshop.php")
    suspend fun getNotifyShop(@Body requestBody: RequestBody): ArrayList<NotifycationModel>

    @POST("notification/func/getnotifycard.php")
    suspend fun getNotifyCard(@Body requestBody: RequestBody): ArrayList<NotifycationCardModel>

}