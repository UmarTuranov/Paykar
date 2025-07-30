package tj.paykar.shop.data.repository

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.CarParkModel
import tj.paykar.shop.data.model.LastVisitModel

interface CarParkService {
    @POST("/manager/api/appqrcode.php")
    suspend fun checkCarNumber(@Body requestBody: RequestBody): Response<CarParkModel?>

    @POST("/manager/api/lastvisit.php")
    suspend fun getLastVisit(@Body requestBody: RequestBody): Response<LastVisitModel>
}