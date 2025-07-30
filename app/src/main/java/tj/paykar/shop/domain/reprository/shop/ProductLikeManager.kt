package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.ProductLikeModel

interface ProductLikeManager {
    suspend fun productLike(productId: Int, userId: Int): ArrayList<ProductLikeModel>
}