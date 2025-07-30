package tj.paykar.shop.data.model.home

import tj.paykar.shop.data.model.shop.NotificationGroupModel
import tj.paykar.shop.data.model.shop.PromoModel
import tj.paykar.shop.data.model.VacanciesModel
import tj.paykar.shop.data.model.shop.OrderStatusModel

data class HomeModel(
    val sliderList: ArrayList<BaseSliderModel>,
    val orderStatus: OrderStatusModel?,
    val splashList: ArrayList<SplashGroupModel>,
    val storyList: ArrayList<StoriesGroupModel>,
    val notificationList: ArrayList<NotificationGroupModel>,
    val promoList: ArrayList<PromoModel>,
    val operatingMode: String?,
    val vacancyList: ArrayList<VacanciesModel>,
    val AndroidVersionApp: String?,
    val walletIsActive: Boolean?,
    val forceUpdate: Boolean?,
    val presentDetail: PresentDetailModel?
)

data class PresentDetailModel(
    val title: String?,
    val description: String?,
    val image: String?
)