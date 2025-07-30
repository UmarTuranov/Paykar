package tj.paykar.shop.data.repository.shop

import retrofit2.Response
import retrofit2.http.GET
import tj.paykar.shop.data.model.shop.VacationDayModel

interface VacationDayService {
    @GET("api/restday/restday.php")
    suspend fun restDay(): Response<VacationDayModel>
}