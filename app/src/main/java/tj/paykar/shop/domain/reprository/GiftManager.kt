package tj.paykar.shop.domain.reprository

import retrofit2.Response
import tj.paykar.shop.data.model.gift.DeferredGiftModel
import tj.paykar.shop.data.model.gift.PromotionModel
import tj.paykar.shop.data.model.gift.ReceiveGiftModel

interface GiftManager {

    suspend fun checkGift(cardCode: String): Response<PromotionModel>

    suspend fun receiveGift(
        phone: String,
        cardCode: String,
        unit: String,
        userId: Int,
        giftId: Int,
        fullName: String,
        categoryGift: String,
        sum: Double,
        numberCheck: String,
        type: String
    ): Response<ReceiveGiftModel>

    suspend fun deferGift(
        phone: String,
        cardCode: String,
        unit: String,
        userId: Int,
        giftId: Int,
        fullName: String,
        categoryGift: String,
        sum: Double,
        numberCheck: String
    ): Response<ReceiveGiftModel>

    suspend fun deferredGiftList(
        cardCode: String
    ): Response<DeferredGiftModel>
}