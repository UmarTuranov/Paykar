package tj.paykar.shop.domain.reprository

import tj.paykar.shop.data.model.home.StoriesModel

interface StoriesManager {
    suspend fun getStories(): ArrayList<StoriesModel>
}