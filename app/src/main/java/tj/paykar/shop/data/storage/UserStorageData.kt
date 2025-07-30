package tj.paykar.shop.data.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.*
import tj.paykar.shop.data.model.*
import tj.paykar.shop.data.model.loyalty.Cards
import tj.paykar.shop.data.model.loyalty.CuponContentModel
import tj.paykar.shop.data.model.loyalty.DocumentDetails
import tj.paykar.shop.data.model.loyalty.HistoryCardModel
import tj.paykar.shop.data.model.shop.NotifycationModel
import tj.paykar.shop.data.model.shop.ProductItems
import tj.paykar.shop.data.model.shop.ProductOrder
import tj.paykar.shop.data.repository.UserStorage
import java.lang.reflect.Type

class UserStorageData (context: Context): UserStorage {

    private var userData: String = USER_DATA
    private var pref: SharedPreferences? = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
    private val id: Int = pref?.getInt(USER_ID, 0) ?: 0
    private val firstName: String = pref?.getString(USER_FIRST_NAME, "") ?: ""
    private val lastName: String = pref?.getString(USER_LAST_NAME, "") ?: ""
    private val phone: String = pref?.getString(USER_PHONE, "") ?: ""
    val userBirthday: String = pref?.getString(USER_BIRTHDAY, "") ?: ""
    private val accessNotification: Boolean = pref?.getBoolean(USER_ACCESS_NOTIFICATION, false) ?: false
    private val accessLocation: Boolean = pref?.getBoolean(USER_ACCESS_LOCATION, false) ?: false
    private val openMap: Boolean = pref?.getBoolean(OPEN_MAP, false) ?: false
    private val fToken: String = pref?.getString(FIREBASE_TOKEN, "") ?: ""
    private val basketCount: Int = pref?.getInt(BASKET_COUNT, 0) ?: 0
    private val presentShow: Boolean = pref?.getBoolean(PRESENT_SHOW, false) ?: false

    private val cardClientId: Int = pref?.getInt(INFO_CARD_CLIENT_ID, 0) ?: 0
    private val cardAuthorized: String = pref?.getString(INFO_CARD_AUTHORIZED, "") ?: ""
    private val cardFirstName: String = pref?.getString(INFO_CARD_FIRST_NAME, "") ?: ""
    private val cardLastName: String = pref?.getString(INFO_CARD_LAST_NAME, "") ?: ""
    private val cardSecondName: String = pref?.getString(INFO_CARD_SECOND_NAME, "") ?: ""
    private val cardGender: String = pref?.getString(INFO_CARD_GENDER, "") ?: ""
    private val cardBirthday: String = pref?.getString(INFO_CARD_BIRTHDAY, "") ?: ""
    private val cardPhone: String = pref?.getString(INFO_CARD_PHONE, "") ?: ""
    private val cardToken: String = pref?.getString(INFO_CARD_TOKEN, "") ?: ""
    private val cardFToken: String = pref?.getString(INFO_CARD_FTOKEN, "") ?: ""
    private val cardBalance: String = pref?.getString(INFO_CARD_BALANCE, "") ?: ""
    private val cardCodeLong: String = pref?.getString(INFO_CARD_CODE, "") ?: ""
    private val cardShortCode: String = pref?.getString(INFO_CARD_SHORT_CODE, "") ?: ""
    private val cardLastUpdate: String = pref?.getString(INFO_CARD_UPDATE, "") ?: ""
    private val cardAccumulateOnly: Boolean = pref?.getBoolean(INFO_CARD_ACCUMULATE, false) ?: false
    private val cardIsPhoneConfirmed: Boolean = pref?.getBoolean(INFO_CARD_CONFIRMED, false) ?: false
    private val cardClientStatus: String = pref?.getString("clientStatus", "") ?: ""

    val lastSendTime: Long = pref?.getLong("last_send_time", 0) ?: 0
    val sendsToday: Int = pref?.getInt("sends_today", 0) ?: 0

    private val selectedPreferences: Boolean = pref?.getBoolean("selectedPreferences", false) ?: false
    private val gift: String = pref?.getString("gift", "") ?: ""

    fun saveNotificationList(notificationList: ArrayList<NotifycationModel>) {
        val editor = pref?.edit()
        val gson = Gson()
        val json: String = gson.toJson(notificationList)
        editor?.putString("notificationList", json)
        editor?.apply()
    }

    fun getNotificationList(): ArrayList<NotifycationModel> {
        val gson = Gson()
        val json: String = pref?.getString("notificationList", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<NotifycationModel>>() {}.type
        return try {
            gson.fromJson(json, type)
        }catch (_:Exception){
            arrayListOf()
        }
    }

    fun saveSentConfirmCodeCount(): Boolean {
        val editor = pref?.edit()
        editor?.putLong("last_send_time", System.currentTimeMillis())
        editor?.putInt("sends_today", sendsToday + 1)
        editor?.apply()
        return true
    }

    fun resetSendCount() {
        val editor = pref?.edit()
        editor?.putInt("sends_today", 0)
        editor?.apply()
    }

    fun saveSplashUrl(url: String) {
        val editor = pref?.edit()
        editor?.putString("splashUrl", url)
        editor?.apply()
    }

    fun getSplashUrl(): String {
        return pref?.getString("splashUrl", "") ?: ""
    }

    fun saveProfileBgUrl(url: String) {
        val editor = pref?.edit()
        editor?.putString("profileBgUrl", url)
        editor?.apply()
    }

    fun getProfileBgUrl(): String {
        return pref?.getString("profileBgUrl", "") ?: ""
    }

    fun saveOperatingModeUrl(url: String) {
        val editor = pref?.edit()
        editor?.putString("operatingModeUrl", url)
        editor?.apply()
    }

    fun getOperatingModeUrl(): String? {
        return pref?.getString("operatingModeUrl", "")
    }

    fun selectPrefOrNo (): Boolean {
        return selectedPreferences
    }

    fun saveSelectedPref() {
        val editor = pref?.edit()
        editor?.putBoolean("selectedPreferences", true)
        editor?.apply()
    }

    override fun getUser(): UserStorageModel {
        return UserStorageModel(
            id, firstName, lastName, phone
        )
    }

    override fun saveUser(userStorage: UserStorageModel): Boolean {
        val editor = pref?.edit()
        editor?.putInt(USER_ID, userStorage.id)
        editor?.putString(USER_FIRST_NAME, userStorage.firstName ?: "")
        editor?.putString(USER_LAST_NAME, userStorage.lastName ?: "")
        editor?.putString(USER_PHONE, userStorage.phone ?: "")
        editor?.apply()
        return true
    }

    override fun updateUser(userUpdateModel: UserUpdateModel): Boolean {
        val editor = pref?.edit()
        editor?.putString(USER_FIRST_NAME, userUpdateModel.firstName)
        editor?.putString(USER_LAST_NAME, userUpdateModel.lastName)
        editor?.putString(USER_PHONE, "992${userUpdateModel.phone}")
        editor?.putString(USER_BIRTHDAY, userUpdateModel.birthday)
        editor?.apply()
        return true
    }

    @SuppressLint("CommitPrefEdits")
    override fun getYandexMap(): Boolean {
        return openMap
    }

    @SuppressLint("CommitPrefEdits")
    override fun saveYandexMap(openMap: Boolean): Boolean {
        val editor = pref?.edit()
        editor?.putBoolean(OPEN_MAP, openMap)
        editor?.apply()
        return openMap
    }

    fun saveBasketProductList(productList: ArrayList<ProductItems>) {
        val editor = pref?.edit()
        val gson = Gson()
        val json: String = gson.toJson(productList)
        editor?.putString(BASKET_ITEMS, json)
        editor?.apply()
    }

    fun getBasketProductList(): ArrayList<ProductItems>? {
        val gson = Gson()
        val json: String = pref?.getString(BASKET_ITEMS, null) ?: ""
        val type: Type = object : TypeToken<ArrayList<ProductItems>?>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveHistoryProduct(productList: ArrayList<ProductOrder>){
        val editor = pref?.edit()
        editor?.remove(HISTORY_PRODUCT)
        val gson = Gson()
        val json: String = gson.toJson(productList)
        editor?.putString(HISTORY_PRODUCT, json)
        editor?.apply()
    }

    fun getHistoryProduct(): ArrayList<ProductOrder>? {
        val gson = Gson()
        val json: String = pref?.getString(HISTORY_PRODUCT, null) ?: ""
        val type: Type = object : TypeToken<ArrayList<ProductOrder>?>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCardHistoryProduct(productList: ArrayList<DocumentDetails>){
        val editor = pref?.edit()
        editor?.remove(HISTORY_PRODUCT_CARD)
        val gson = Gson()
        val json: String = gson.toJson(productList)
        editor?.putString(HISTORY_PRODUCT_CARD, json)
        editor?.apply()
    }

    fun getCardHistoryProduct(): ArrayList<DocumentDetails>? {
        val gson = Gson()
        val json: String = pref?.getString(HISTORY_PRODUCT_CARD, null) ?: ""
        val type: Type = object : TypeToken<ArrayList<DocumentDetails>?>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCardHistory(historyList: ArrayList<HistoryCardModel>){
        val editor = pref?.edit()
        editor?.remove(HISTORY_CARD)
        val gson = Gson()
        val json: String = gson.toJson(historyList)
        editor?.putString(HISTORY_CARD, json)
        editor?.apply()
    }

    fun getCardHistory(): ArrayList<HistoryCardModel>? {
        val gson = Gson()
        val json: String = pref?.getString(HISTORY_CARD, null) ?: ""
        val type: Type = object : TypeToken<ArrayList<HistoryCardModel>?>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCardList(cards: ArrayList<Cards>) {
        val editor = pref?.edit()
        editor?.remove(PROMOCARD_LIST)
        val gson = Gson()
        val json: String = gson.toJson(cards)
        editor?.putString(PROMOCARD_LIST, json)
        editor?.apply()
    }

    fun getCardList(): ArrayList<Cards>? {
        val gson = Gson()
        val json: String = pref?.getString(PROMOCARD_LIST, null) ?: ""
        val type: Type = object : TypeToken<ArrayList<Cards>?>() {}.type
        return gson.fromJson(json, type)
    }

    fun savePromoList(contentList: ArrayList<CuponContentModel>) {
        val editor = pref?.edit()
        editor?.remove(PROMOCODE_LIST)
        val gson = Gson()
        val json: String = gson.toJson(contentList)
        editor?.putString(PROMOCODE_LIST, json)
        editor?.apply()
    }

    fun getPromoList(): ArrayList<CuponContentModel>? {
        val gson = Gson()
        val json: String = pref?.getString(PROMOCODE_LIST, null) ?: ""
        val type: Type = object : TypeToken<ArrayList<CuponContentModel>?>() {}.type
        return gson.fromJson(json, type)
    }

    @SuppressLint("CommitPrefEdits")
    fun saveFirebaseToken(token: String): Boolean {
        val editor = pref?.edit()
        editor?.putString(FIREBASE_TOKEN, token)
        editor?.apply()
        return true
    }

    fun getFirebaseToken(): String {
        return fToken
    }

    @SuppressLint("CommitPrefEdits")
    fun cleanStorage() {
        val editor = pref?.edit()
        editor?.clear()
        editor?.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun savePhone(phone: String) {
        val editor = pref?.edit()
        editor?.putString(USER_PHONE, phone)
        editor?.apply()
    }

    fun getPhone(): String {
        return phone
    }

    @SuppressLint("CommitPrefEdits")
    fun saveCardPhone(phone: String) {
        val editor = pref?.edit()
        editor?.putString(INFO_CARD_PHONE, phone)
        editor?.apply()
    }

    fun getCardPhone(): String {
        return cardPhone
    }

    fun saveInfoCard(userCardStorage: UserCardStorageModel){
        val editor = pref?.edit()
        editor?.putInt(INFO_CARD_CLIENT_ID, userCardStorage.clientId)
        editor?.putString(INFO_CARD_AUTHORIZED, userCardStorage.authorized ?: "")
        editor?.putString(INFO_CARD_FIRST_NAME, userCardStorage.firstName ?: "")
        editor?.putString(INFO_CARD_LAST_NAME, userCardStorage.lastName ?: "")
        editor?.putString(INFO_CARD_SECOND_NAME, userCardStorage.secondName ?: "")
        editor?.putString(INFO_CARD_GENDER, userCardStorage.gender ?: "")
        editor?.putString(INFO_CARD_BIRTHDAY, userCardStorage.birthday ?: "")
        editor?.putString(INFO_CARD_PHONE, userCardStorage.phone ?: "")
        editor?.putString(INFO_CARD_CODE, userCardStorage.cardCode ?: "")
        editor?.putString(INFO_CARD_SHORT_CODE, userCardStorage.shortCardCode ?: "")
        editor?.putString(INFO_CARD_TOKEN, userCardStorage.token ?: "")
        editor?.putString(INFO_CARD_FTOKEN, userCardStorage.fToken ?: "")
        editor?.putString(INFO_CARD_BALANCE, userCardStorage.balance ?: "")
        editor?.putString(INFO_CARD_UPDATE, userCardStorage.lastUpdate ?: "")
        editor?.putBoolean(INFO_CARD_ACCUMULATE, userCardStorage.accumulateOnly ?: false)
        editor?.putBoolean(INFO_CARD_CONFIRMED, userCardStorage.IsPhoneConfirmed ?: false)
        editor?.apply()
    }

    fun getInfoCard(): UserCardStorageModel {
        return UserCardStorageModel(cardClientId,
            cardAuthorized, cardFirstName, cardLastName, cardSecondName, cardGender, cardBirthday, cardPhone, cardToken,
            cardFToken, cardBalance, cardCodeLong, cardShortCode, cardLastUpdate, cardAccumulateOnly, cardIsPhoneConfirmed
        )
    }

    fun cleanInfoCard() {
        val editor = pref?.edit()
        editor?.remove(INFO_CARD_CLIENT_ID)
        editor?.remove(INFO_CARD_AUTHORIZED)
        editor?.remove(INFO_CARD_FIRST_NAME)
        editor?.remove(INFO_CARD_LAST_NAME)
        editor?.remove(INFO_CARD_SECOND_NAME)
        editor?.remove(INFO_CARD_GENDER)
        editor?.remove(INFO_CARD_BIRTHDAY)
        editor?.remove(INFO_CARD_PHONE)
        editor?.remove(INFO_CARD_TOKEN)
        editor?.remove(INFO_CARD_FTOKEN)
        editor?.remove(INFO_CARD_BALANCE)
        editor?.remove(INFO_CARD_CODE)
        editor?.remove(INFO_CARD_SHORT_CODE)
        editor?.remove(INFO_CARD_UPDATE)
        editor?.remove(INFO_CARD_ACCUMULATE)
        editor?.remove(INFO_CARD_CONFIRMED)
        editor?.apply()
    }

    fun updateInfoCard(key: String, value: String) {
        val editor = pref?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun saveClientStatus(status: String) {
        val editor = pref?.edit()
        editor?.putString("clientStatus", status)
        editor?.apply()
    }

    fun getClientStatus(): String {
        return cardClientStatus
    }

    fun saveAccessNotification(value: Boolean) {
        val editor = pref?.edit()
        editor?.putBoolean(USER_ACCESS_NOTIFICATION, value)
        editor?.apply()
    }

    fun getAccessNotification(): Boolean {
        return accessNotification
    }

    fun saveAccessLocation(value: Boolean) {
        val editor = pref?.edit()
        editor?.putBoolean(USER_ACCESS_LOCATION, value)
        editor?.apply()
    }

    fun getAccessLocation(): Boolean {
        return accessLocation
    }

    fun savePresentShow(value: Boolean){
        val editor = pref?.edit()
        editor?.putBoolean(PRESENT_SHOW, value)
        editor?.apply()
    }

    fun getPresentShow(): Boolean {
        return presentShow
    }

    fun getUserId(): Int {
        return id
    }

    fun saveBasketCount(count: Int) {
        val editor = pref?.edit()
        editor?.putInt(BASKET_COUNT, count)
        editor?.apply()
    }

    fun getBasketCount(): Int {
        return basketCount
    }
}