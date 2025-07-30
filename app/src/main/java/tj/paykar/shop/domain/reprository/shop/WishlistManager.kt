package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.WishlistModel

interface WishlistManager {

    suspend fun wishlistItems(userId: Int): WishlistModel

    suspend fun addToWishlist(userId: Int, productId: Int, quantity: Int)

    suspend fun deleteWishlist(userId: Int, productId: Int)

    suspend fun cleanWishlist(userId: Int)
}