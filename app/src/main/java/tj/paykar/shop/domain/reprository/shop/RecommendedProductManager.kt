package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.RecommendationProductModel

interface RecommendedProductManager {
    suspend fun productList(userId: Int): ArrayList<RecommendationProductModel>
}