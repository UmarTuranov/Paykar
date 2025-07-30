package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.MyDevicesModel

interface MyDevicesService {
    @POST("api/devices/getAllDevices.php")
    suspend fun getDevices(@Body requestBody: RequestBody): ArrayList<MyDevicesModel>
}