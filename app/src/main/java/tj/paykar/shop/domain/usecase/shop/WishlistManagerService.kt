package tj.paykar.shop.domain.usecase.shop

import tj.paykar.shop.data.model.shop.WishlistModel
import tj.paykar.shop.data.repository.shop.WishlistService
import tj.paykar.shop.data.service.RetrofitService

class WishlistManagerService: tj.paykar.shop.domain.reprository.shop.WishlistManager {

    private val retrofitService = RetrofitService()

    override suspend fun wishlistItems(userId: Int): WishlistModel {
        val request = retrofitService.sendRequest()
        val response = request.create(WishlistService::class.java)
        return response.getWishlist(userId)
    }

    override suspend fun addToWishlist(userId: Int, productId: Int, quantity: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(WishlistService::class.java)
        return response.addToWishlist(userId, productId, quantity)
    }

    override suspend fun deleteWishlist(userId: Int, productId: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(WishlistService::class.java)
        return response.deleteProductWishlist(userId, productId)
    }

    override suspend fun cleanWishlist(userId: Int) {
        val request = retrofitService.sendRequest()
        val response = request.create(WishlistService::class.java)
        return response.deleteAllWishlist(userId)
    }

}