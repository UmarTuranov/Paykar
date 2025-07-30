package tj.paykar.shop.data.repository.wallet

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IpAddressService {
    @GET("/")
    suspend fun getIpAddress(@Query("format") format: String = "json"): Response<JsonObject>
}