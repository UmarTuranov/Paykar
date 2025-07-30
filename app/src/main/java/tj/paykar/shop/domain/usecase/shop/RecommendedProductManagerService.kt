package tj.paykar.shop.domain.usecase.shop

import tj.paykar.shop.data.model.shop.RecommendationProductModel
import tj.paykar.shop.data.repository.shop.RecommendedProductService
import tj.paykar.shop.data.service.RetrofitService

class RecommendedProductManagerService:
    tj.paykar.shop.domain.reprository.shop.RecommendedProductManager {
    override suspend fun productList(userId: Int): ArrayList<RecommendationProductModel> {
        val request = RetrofitService().sendRequest()
        val response = request.create(RecommendedProductService::class.java)
        return response.getProducts(userId)
    }
}