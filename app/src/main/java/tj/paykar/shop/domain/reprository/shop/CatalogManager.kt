package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.CatalogModel
import tj.paykar.shop.data.model.shop.CatalogSectionModel
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel

interface CatalogManager {

    suspend fun catalogList(): ArrayList<CatalogModel>

    suspend fun sectionList(userId: Int, position: Int): CatalogSectionModel

    suspend fun productList(userId: Int, position: Int): ArrayList<CatalogSectionProductModel>

}