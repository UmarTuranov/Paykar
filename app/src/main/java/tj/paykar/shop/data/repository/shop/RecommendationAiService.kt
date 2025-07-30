package tj.paykar.shop.data.repository.shop

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel

interface RecommendationAiService {
    @GET("bitrix/api/ai/recomendationAi.php")
    suspend fun getProducts(@Query("userId") userId: Int): Response<ArrayList<CatalogSectionProductModel>>
}