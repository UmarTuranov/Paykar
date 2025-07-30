package tj.paykar.shop.domain.usecase

import retrofit2.Response
import tj.paykar.shop.data.repository.PresentService
import tj.paykar.shop.data.service.RetrofitService
import tj.paykar.shop.domain.reprository.PresentManager

class PresentManagerService: PresentManager {

    private val retrofitService = RetrofitService()

    override suspend fun slideItems(): Response<ArrayList<String>> {
        val request = retrofitService.sendRequest()
        val response = request.create(PresentService::class.java)
        return response.getPresentSlide()
    }

}