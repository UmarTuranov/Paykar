package tj.paykar.shop.domain.usecase.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.VacationDayModel
import tj.paykar.shop.data.repository.shop.VacationDayService
import tj.paykar.shop.data.service.RetrofitService

class VacationDayManagerService: tj.paykar.shop.domain.reprository.shop.VacationDayManager {
    override suspend fun restDay(): Response<VacationDayModel> {
        val request = RetrofitService().serverRequest()
        val response = request.create(VacationDayService::class.java)
        return response.restDay()
    }
}