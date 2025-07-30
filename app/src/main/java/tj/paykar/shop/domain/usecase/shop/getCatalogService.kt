package tj.paykar.shop.domain.usecase.shop

import tj.paykar.shop.data.model.shop.CatalogModel
import tj.paykar.shop.data.model.shop.CatalogSectionModel
import tj.paykar.shop.data.model.shop.CatalogSectionProductModel
import tj.paykar.shop.data.repository.shop.CatalogService
import tj.paykar.shop.data.service.RetrofitService

class getCatalogService: tj.paykar.shop.domain.reprository.shop.CatalogManager {

    private val retrofitService = RetrofitService()

    override suspend fun catalogList(): ArrayList<CatalogModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(CatalogService::class.java)
        return response.getCatalog()
    }

    override suspend fun sectionList(userId: Int, position: Int): CatalogSectionModel {
        val request = retrofitService.sendRequest()
        val response = request.create(CatalogService::class.java)
        return response.getSections(userId, position)
    }

    override suspend fun productList(userId: Int, position: Int): ArrayList<CatalogSectionProductModel> {
        val request = retrofitService.sendRequest()
        val response = request.create(CatalogService::class.java)
        return response.getProducts(userId, position)
    }

}