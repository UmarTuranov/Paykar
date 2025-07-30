package tj.paykar.shop.domain.reprository

import retrofit2.Response
import tj.paykar.shop.data.model.loyalty.ChipCardModel
import tj.paykar.shop.data.model.loyalty.CuponContentModel
import tj.paykar.shop.data.model.loyalty.HistoryCardModel
import tj.paykar.shop.data.model.loyalty.InfoCardModel
import tj.paykar.shop.data.model.loyalty.PromoCardModel
import tj.paykar.shop.data.model.loyalty.UserCardModel

interface CardManager {

    suspend fun sendConfCode(phone: String): String

    suspend fun checkConfCode(phone: String, code: String): Response<UserCardModel>

    suspend fun sendSmsCode(phone: String, cardCode: String)

    suspend fun checkSmsCode(phone: String, cardCode: String, confCode: String): Boolean

    suspend fun saveCardInfo(clientId: Int, lastName: String, firstName: String, secondName: String, birthdate: String, phone: String, email: String, city: String, street: String, house: String, genderId: Int, phoneEnable: Boolean): Response<InfoCardModel>

    suspend fun historyCardList(token: String): ArrayList<HistoryCardModel>

    suspend fun promoList(): ArrayList<PromoCardModel>

    suspend fun cardInfo(phone: String, cardCode: String): InfoCardModel

    suspend fun checkCardNumber(shortCardCode: String): Response<InfoCardModel>

    suspend fun cardCheck(phone: String): Response<InfoCardModel>

    suspend fun writeNotify(token: String, fToken: String, modelName: String, version: String)

    suspend fun saveUser(fname: String, birthday: String, adress: String, phone: String, email: String, cardcode: String, ftoken: String, deviceModel: String, typeOS: String, versionOS: String)

    suspend fun chipList(token: String): ArrayList<ChipCardModel>

    suspend fun promoCode(token: String): Response<UserCardModel>

    suspend fun promoContent(token: String): Response<ArrayList<CuponContentModel>>
}