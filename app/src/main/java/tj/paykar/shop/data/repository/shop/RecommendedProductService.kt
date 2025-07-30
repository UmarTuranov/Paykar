package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.RecommendationProductModel

interface RecommendedProductService {
    @GET("/bitrix/api/basket/recommendationmodel.php")
    suspend fun getProducts( @Query("userId") userId: Int): ArrayList<RecommendationProductModel>

}