package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.CatalogSectionProductModel
import tj.paykar.shop.data.model.shop.SearchRecomendModel

interface SearchManager {

    suspend fun searchTopList(): ArrayList<String>

    suspend fun recommendProductList(userId: Int): ArrayList<SearchRecomendModel>

    suspend fun searchProductList(userId: Int, searchText: String): ArrayList<CatalogSectionProductModel>

}