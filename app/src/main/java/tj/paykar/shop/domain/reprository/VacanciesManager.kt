package tj.paykar.shop.domain.reprository

import tj.paykar.shop.data.model.VacanciesModel

interface VacanciesManager {
    suspend fun vacanciesList(): ArrayList<VacanciesModel>
}