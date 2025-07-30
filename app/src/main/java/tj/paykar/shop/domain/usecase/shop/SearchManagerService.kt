package tj.paykar.shop.domain.usecase.shop

import tj.paykar.shop.data.model.shop.CatalogSectionProductModel
import tj.paykar.shop.data.model.shop.SearchRecomendModel
import tj.paykar.shop.data.repository.shop.SearchService
import tj.paykar.shop.data.service.RetrofitService

class SearchManagerService: tj.paykar.shop.domain.reprository.shop.SearchManager {

    private val retrofitService = RetrofitService()

    override suspend fun searchTopList(): ArrayList<String> {
        val request = retrofitService.sendRequest()
        val response = request.create(SearchService::class.java)
        return response.getSearchTop()
    }

    override suspend fun recommendProductList(userId: Int): ArrayList<SearchRecomendModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(SearchService::class.java)
        return response.getRecommend(userId)
    }

    override suspend fun searchProductList(userId: Int, searchText: String): ArrayList<CatalogSectionProductModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(SearchService::class.java)
        return response.searchProduct(userId, searchText)
    }


}