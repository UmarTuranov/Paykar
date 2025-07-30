package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.PromoModel

interface PromoManager {
    suspend fun promoList(): ArrayList<PromoModel>
}