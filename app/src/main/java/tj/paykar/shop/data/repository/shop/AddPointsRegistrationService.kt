package tj.paykar.shop.data.repository.shop

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AddPointsRegistrationService {
    @POST("api/processing/correction.php")
    suspend fun addPointsToClient(@Body requestBody: RequestBody)
}