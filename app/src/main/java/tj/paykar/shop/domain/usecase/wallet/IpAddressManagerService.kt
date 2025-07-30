package tj.paykar.shop.domain.usecase.wallet

import android.util.Log
import com.google.gson.JsonObject
import retrofit2.Response
import tj.paykar.shop.data.repository.wallet.IpAddressService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.wallet.IpAddressManager

class IpAddressManagerService: IpAddressManager {
    private val request = RetrofitService().ipAddressRequest()
    private val response = request.create(IpAddressService::class.java)
    override suspend fun getIpAddress(): Response<JsonObject> {
        Log.d("--I IpAddress", "Requested")
        return response.getIpAddress()
    }
}