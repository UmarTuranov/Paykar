package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.NotifycationModel
import tj.paykar.shop.data.model.loyalty.NotifycationCardModel

interface NotifycationManager {

    suspend fun notifyList(phone: String, token: String): ArrayList<NotifycationModel>

    suspend fun notifyCardList(phone: String, token: String): ArrayList<NotifycationCardModel>
}