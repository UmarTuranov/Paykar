package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.WishlistModel

interface WishlistService {

    @GET("bitrix/api/basket/wishlist.php")
    suspend fun getWishlist( @Query("userId") userId: Int): WishlistModel

    @GET("bitrix/api/basket/addwishlistbyproductid.php")
    suspend fun addToWishlist( @Query("userId") userId: Int,
                               @Query("productId") productId: Int,
                               @Query("quantity") quantity: Int )

    @GET("bitrix/api/basket/deletewishlist.php")
    suspend fun deleteProductWishlist( @Query("userId") userId: Int,
                                       @Query("productId") productId: Int)

    @GET("bitrix/api/basket/deleteallwishlist.php")
    suspend fun deleteAllWishlist( @Query("userId") userId: Int)
}