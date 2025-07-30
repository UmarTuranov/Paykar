package tj.paykar.shop.domain.usecase

import tj.paykar.shop.data.model.VacanciesModel
import tj.paykar.shop.data.repository.VacanciesService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.VacanciesManager

class VacanciesManagerService: VacanciesManager {
    override suspend fun vacanciesList(): ArrayList<VacanciesModel> {
        val request = RetrofitService().paykarJobRequest()
        val response = request.create(VacanciesService::class.java)
        return response.vacanciesList()
    }
}