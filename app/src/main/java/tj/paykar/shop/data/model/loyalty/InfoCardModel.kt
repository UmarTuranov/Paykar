package tj.paykar.shop.data.model.loyalty

data class InfoCardModel(
    val ClientId: Int?,
    val AcountId: Int?,
    val LastName: String?,
    val FirstName: String?,
    val SurName: String?,
    val Email: String?,
    val City: String?,
    val Street: String?,
    val Birthday: String?,
    val Balance: Double?,
    val AccumulateOnly: Boolean?,
    val Blocked: Boolean?,
    val IsPhoneConfirmed: Boolean?,
    val PhoneMobile: String?,
    val CardCode: String?,
    val GenderName: String
)
