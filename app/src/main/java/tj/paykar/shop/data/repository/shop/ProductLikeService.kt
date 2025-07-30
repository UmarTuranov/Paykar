package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.ProductLikeModel

interface ProductLikeService {
    @GET("bitrix/api/catalog/productLike.php")
    suspend fun productLike(@Query("productId") productId: Int, @Query("userId") userId: Int): ArrayList<ProductLikeModel>
}