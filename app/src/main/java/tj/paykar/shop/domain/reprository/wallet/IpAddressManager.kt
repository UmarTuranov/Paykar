package tj.paykar.shop.domain.reprository.wallet

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response

interface IpAddressManager {
    suspend fun getIpAddress(): Response<JsonObject>
}