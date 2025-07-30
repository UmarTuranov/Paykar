package tj.paykar.shop.domain.usecase.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.OrderStatusModel
import tj.paykar.shop.data.repository.shop.OrderStatusService
import tj.paykar.shop.data.service.RetrofitService

class OrderStatusManagerService: tj.paykar.shop.domain.reprository.shop.OrderStatusManager {
    override suspend fun getOrderStatus(userId: Int): Response<OrderStatusModel> {
        val request = RetrofitService().sendRequest()
        val response = request.create(OrderStatusService::class.java)
        return response.getOrderStatus(userId)
    }
}