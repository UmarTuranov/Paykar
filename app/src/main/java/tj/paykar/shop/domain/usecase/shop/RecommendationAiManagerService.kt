package tj.paykar.shop.domain.usecase.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel
import tj.paykar.shop.data.repository.shop.RecommendationAiService
import tj.paykar.shop.data.service.RetrofitService

class RecommendationAiManagerService:
    tj.paykar.shop.domain.reprository.shop.RecommendationAiManager {
    override suspend fun getProducts(userId: Int):  Response<ArrayList<CatalogSectionProductModel>> {
        val request = RetrofitService().sendRequest()
        val response = request.create(RecommendationAiService::class.java)
        return response.getProducts(userId)
    }
}