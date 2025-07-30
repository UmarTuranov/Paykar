package tj.paykar.shop.data.repository.shop

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.AddBasketResponseModel
import tj.paykar.shop.data.model.shop.BasketCountModel
import tj.paykar.shop.data.model.shop.BasketModel

interface BasketService {

    @GET("bitrix/api/basket/basket.php")
    suspend fun getBasket( @Query("userId") userId: Int): BasketModel

    @GET("bitrix/api/basket/addbasketbyproductid.php")
    suspend fun addProductToBasket( @Query("userId") userId: Int,
                                    @Query("productId") productId: Int,
                                    @Query("quantity") quantity: Double ): Response<AddBasketResponseModel>

    @GET("bitrix/api/basket/deletebasket.php")
    suspend fun deleteProductBasket( @Query("userId") userId: Int,
                                     @Query("productId") productId: Int)

    @GET("bitrix/api/basket/deleteallbasket.php")
    suspend fun deleteAllProductBasket( @Query("userId") userId: Int)


    @GET("bitrix/api/basket/basketcount.php")
    suspend fun getBasketCount( @Query("userId") userId: Int): BasketCountModel

}