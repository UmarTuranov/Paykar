package tj.paykar.shop.domain.usecase.shop

import tj.paykar.shop.data.model.shop.ProductLikeModel
import tj.paykar.shop.data.repository.shop.ProductLikeService
import tj.paykar.shop.data.service.RetrofitService

class ProductLikeManagerService: tj.paykar.shop.domain.reprository.shop.ProductLikeManager {
    override suspend fun productLike(productId: Int, userId: Int): ArrayList<ProductLikeModel> {
        val request = RetrofitService().sendRequest()
        val response = request.create(ProductLikeService::class.java)
        return response.productLike(productId, userId)
    }
}