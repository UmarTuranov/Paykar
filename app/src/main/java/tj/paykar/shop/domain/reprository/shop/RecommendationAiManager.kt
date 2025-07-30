package tj.paykar.shop.domain.reprository.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel

interface RecommendationAiManager {
    suspend fun getProducts(userId: Int): Response<ArrayList<CatalogSectionProductModel>>
}