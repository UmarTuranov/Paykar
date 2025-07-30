package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.ExchangeRateModel
import retrofit2.Response

interface ExchangeRateManager {
    suspend fun getExchangeRate(): Response<ExchangeRateModel>
}