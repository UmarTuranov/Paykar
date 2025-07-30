package tj.paykar.shop.data
import tj.paykar.shop.presentation.authorization.LoginActivity
import tj.paykar.shop.presentation.card.authorization.BindCardActivity
import tj.paykar.shop.presentation.shop.ShopActivity

const val USER_DATA = "userData"
const val USER_FID = "userFid"
const val USER_ID = "userId"
const val USER_FIRST_NAME = "userFirstName"
const val USER_BIRTHDAY = "userBirthday"
const val USER_LAST_NAME = "userLastName"
const val USER_PHONE = "userPhone"
const val USER_CARD = "userCard"
const val USER_TOKEN = "userToken"
const val USER_SHORT_CARD_CODE = "userShortCardCode"
const val USER_CARD_CODE = "userCardCode"
const val USER_ADDRESS = "userAddressDelivery"
const val USER_ACCESS_CODE = "userAccessCode"
const val USER_ACCESS_NOTIFICATION = "userAccessNotification"
const val USER_ACCESS_LOCATION = "userAccessLocation"
const val USER_ACCESS_AUTHORIZED = "userAuthorized"
lateinit var AUTH: LoginActivity
lateinit var CARD: BindCardActivity
lateinit var CATALOG_ACTIVITY: ShopActivity
const val OPEN_MAP = "openMap"
const val BASKET_ITEMS = "basketItems"
const val FIREBASE_TOKEN = "fToken"
const val HISTORY_PRODUCT = "productHistory"
const val HISTORY_CARD = "historyCard"
const val PROMOCODE_LIST = "promoCode"
const val PROMOCARD_LIST = "promoCard"
const val HISTORY_PRODUCT_CARD = "productHistoryCard"
const val INFO_CARD_CLIENT_ID = "infoCardClientId"
const val INFO_CARD_AUTHORIZED = "infoCardAuthorized"
const val INFO_CARD_FIRST_NAME = "infoCardFirstName"
const val INFO_CARD_LAST_NAME = "infoCardLastName"
const val INFO_CARD_SECOND_NAME = "infoCardSecondName"
const val INFO_CARD_GENDER = "infoCardGender"
const val INFO_CARD_BIRTHDAY = "infoCardBirthday"
const val INFO_CARD_PHONE = "infoCardPhone"
const val INFO_CARD_SHORT_CODE = "infoCardShortCode"
const val INFO_CARD_CODE = "infoCardCode"
const val INFO_CARD_TOKEN = "infoCardToken"
const val INFO_CARD_FTOKEN = "infoCardFToken"
const val INFO_CARD_BALANCE = "infoCardBalance"
const val INFO_CARD_UPDATE = "infoCardUpdate"
const val INFO_CARD_ACCUMULATE= "cardAccumulateOnly"
const val INFO_CARD_CONFIRMED = "cardIsPhoneConfirmed"

const val BASKET_COUNT = "basketCount"
const val PRESENT_SHOW = "presentShow"
const val LICENSE_GUIRD = "E7AB2716-A73A-40DF-BD6F-56EEE7A505B0"

//const val WALLET_PSK = "f493b215e14df5986e6e03696345bc4942f94efa"
const val WALLET_PSK = "123456"
const val QR_PAYMENT_ID = 256
const val TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID = 261
const val TRANSFER_TO_BANK_CARDS_PAYMENT_ID = 288
const val REPLENISH_PAYKAR_WALLET_PAYMENT_ID = 258
const val TRANSFER_TO_VISA_CARDS_PAYMENT_ID = 536
const val MOBILE_OPERATORS_CATEGORY_ID = 1
const val WALLETS_CATEGORY_ID = 3
const val PUBLIC_UTILITIES_CATEGORY_ID = 5
const val TRANSFERS_CATEGORY_ID = 17
const val PAYKAR_PARKING_SERVICE_ID = 537
const val CITY_PARKING_SERVICE_ID = 538
const val PAY_ORDER_SERVICE_ID = 540