package tj.paykar.shop.domain.usecase.shop

import tj.paykar.shop.data.model.shop.PromoModel
import tj.paykar.shop.data.repository.shop.PromoService
import tj.paykar.shop.data.service.RetrofitService

class PromoManagerService: tj.paykar.shop.domain.reprository.shop.PromoManager {

    private val retrofitService = RetrofitService()

    override suspend fun promoList(): ArrayList<PromoModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(PromoService::class.java)
        return response.getPromo()
    }

}