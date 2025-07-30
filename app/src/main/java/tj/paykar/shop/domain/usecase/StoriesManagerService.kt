package tj.paykar.shop.domain.usecase

import tj.paykar.shop.data.model.home.StoriesModel
import tj.paykar.shop.data.repository.StoriesService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.StoriesManager

class StoriesManagerService: StoriesManager {
    override suspend fun getStories(): ArrayList<StoriesModel> {
        val request = RetrofitService().serverRequest()
        val response = request.create(StoriesService::class.java)
        return response.getStories()
    }
}