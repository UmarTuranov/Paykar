package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.OrderHistoryModel

interface OrderHistoryManager {

    suspend fun createOrder(userId: Int, deliveryAdress: String, date: String, time: String, comment: String): Int

    suspend fun orderHistoryItems(userId: Int): ArrayList<OrderHistoryModel>

    suspend fun repeatOrder(userId: Int, orderId: Int)

    suspend fun cancellationOrder(orderId: Int)

}