package tj.paykar.shop.domain.usecase.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.AddBasketResponseModel
import tj.paykar.shop.data.model.shop.BasketCountModel
import tj.paykar.shop.data.model.shop.BasketModel
import tj.paykar.shop.data.repository.shop.BasketService
import tj.paykar.shop.data.service.RetrofitService

class getBasketService: tj.paykar.shop.domain.reprository.shop.BasketManager,
    tj.paykar.shop.domain.reprository.shop.BasketEmpty {

    private val retrofitService = RetrofitService()

    override suspend fun basketItems(userId: Int): BasketModel {
        val request = retrofitService.sendRequest()
        val response = request.create(BasketService::class.java)
        return response.getBasket(userId)
    }

    override suspend fun addBasketItem(userId: Int, productId: Int, quantity: Double): Response<AddBasketResponseModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(BasketService::class.java)
        return response.addProductToBasket(userId, productId, quantity)
    }

    override suspend fun deleteBasketItem(userId: Int, productId: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(BasketService::class.java)
        return response.deleteProductBasket(userId, productId)
    }

    override suspend fun cleanBasketItems(userId: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(BasketService::class.java)
        return response.deleteAllProductBasket(userId)
    }

    override suspend fun basketCount(userId: Int): BasketCountModel {
        val request = retrofitService.sendRequest()
        val response = request.create(BasketService::class.java)
        return response.getBasketCount(userId)
    }

    override fun basketEmpty(basket: BasketModel): Boolean {
        return basket.productList.size == 0
    }

}