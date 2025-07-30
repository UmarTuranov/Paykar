package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.CatalogModel
import tj.paykar.shop.data.model.shop.CatalogSectionModel
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel

interface CatalogService {

    @GET("bitrix/api/catalog/catalog.php")
    suspend fun getCatalog(): ArrayList<CatalogModel>

    @GET("bitrix/api/catalog/category.php")
    suspend fun getSections(
        @Query("userId") userId: Int,
        @Query("sectionId") sectionId: Int
    ): CatalogSectionModel

    @GET("bitrix/api/products.php")
    suspend fun getProducts(
        @Query("userId") userId: Int,
        @Query("BlockId") sectionId: Int
    ): ArrayList<CatalogSectionProductModel>

}