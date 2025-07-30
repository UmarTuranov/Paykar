package tj.paykar.shop.domain.reprository.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.AddBasketResponseModel
import tj.paykar.shop.data.model.shop.BasketCountModel
import tj.paykar.shop.data.model.shop.BasketModel

interface BasketManager {

    suspend fun basketItems(userId: Int): BasketModel

    suspend fun addBasketItem(userId: Int, productId: Int, quantity: Double): Response<AddBasketResponseModel>

    suspend fun deleteBasketItem(userId: Int, productId: Int)

    suspend fun cleanBasketItems(userId: Int)

    suspend fun basketCount(userId: Int): BasketCountModel

}