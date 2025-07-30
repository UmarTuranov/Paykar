package tj.paykar.shop.data.repository.wallet

import tj.paykar.shop.data.model.wallet.ExchangeRateModel
import retrofit2.Response
import retrofit2.http.GET

interface ExchangeRateService {
    @GET("api/exchangerates/rates.php")
    suspend fun getExchangeRate(): Response<ExchangeRateModel>
}