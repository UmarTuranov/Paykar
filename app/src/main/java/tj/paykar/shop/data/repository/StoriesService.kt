package tj.paykar.shop.data.repository

import retrofit2.http.POST
import tj.paykar.shop.data.model.home.StoriesModel

interface StoriesService {
    @POST("/admin/func/get_all_stories.php")//    /admin/func/get_all_stories_test.php
    suspend fun getStories(): ArrayList<StoriesModel>
}