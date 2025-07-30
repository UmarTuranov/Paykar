package tj.paykar.shop.data.repository

import retrofit2.Response
import retrofit2.http.GET

interface PresentService {

    @GET("bitrix/api/present/slider.php")
    suspend fun getPresentSlide(): Response<ArrayList<String>>

}