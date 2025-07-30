package tj.paykar.shop.domain.usecase.wallet

import tj.paykar.shop.data.model.wallet.ExchangeRateModel
import tj.paykar.shop.domain.reprository.wallet.ExchangeRateManager
import retrofit2.Response
import tj.paykar.shop.data.repository.wallet.ExchangeRateService
import tj.paykar.shop.data.service.RetrofitService

class ExchangeRateManagerService: ExchangeRateManager {
    override suspend fun getExchangeRate(): Response<ExchangeRateModel> {
        val request = RetrofitService().serverRequest()
        val response = request.create(ExchangeRateService::class.java)
        return response.getExchangeRate()
    }
}