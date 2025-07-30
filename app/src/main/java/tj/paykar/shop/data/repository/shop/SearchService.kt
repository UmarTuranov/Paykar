package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import retrofit2.http.Query
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel
import tj.paykar.shop.data.model.shop.SearchRecomendModel

interface SearchService {

    @GET("bitrix/api/search/searchtop.php")
    suspend fun getSearchTop(): ArrayList<String>

    @GET("bitrix/api/search/recommend.php")
    suspend fun getRecommend(@Query("userId") userId: Int): ArrayList<SearchRecomendModel>

    @GET("bitrix/api/search/getproducts.php")
    suspend fun searchProduct(@Query("userId") userId: Int,
                              @Query("searchText") searchText: String
                              ): ArrayList<CatalogSectionProductModel>

}