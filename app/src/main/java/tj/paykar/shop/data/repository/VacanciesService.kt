package tj.paykar.shop.data.repository

import retrofit2.http.GET
import tj.paykar.shop.data.model.VacanciesModel

interface VacanciesService {
    @GET("bitrix/api/vacancy/list.php")
    suspend fun vacanciesList(): ArrayList<VacanciesModel>
}