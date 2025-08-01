package tj.paykar.shop.data.model.id

data class LoyaltyModel(
    val ClientId: Int?,
    val AcountId: Int?,
    val LastName: String?,
    val FirstName: String?,
    val SurName: String?,
    val IsCoupon: Boolean?,
    val Email: String?,
    val City: String?,
    val Street: String?,
    val Birthday: String?,
    val Balance: Double?,
    val DelayedBonuses: Int?,
    val Turnover: Double?,
    val TurnoverInCompany: Double?,
    val AccumulateOnly: Boolean?,
    val Blocked: Boolean?,
    val AccountType: Int?,
    val IsEmailConfirmed: Boolean?,
    val IsPhoneConfirmed: Boolean?,
    val IsRegisteredOnPortal: Boolean?,
    val CardStatus: String?,
    val ClientStatus: String?,
    val PhoneMobile: String?,
    val CardCode: String?,
    val StartDate: String?,
    val FinishDate: String?,
    val GenderName: String?,
    val LegalAddress: String?,
    val Married: Boolean?,
    val PassportNumber: String?,
    val INN: String?,
    val KPP: String?,
    val TaxStatusId: Int?,
    val TaxStatusName: String?,
    val SumPurchasePeriod: Double?,
    val SumToBuyToOtherStatus: Double?,
    val SumToConfirmStatus: Double?,
    val ControlDate: String?,
    val ControlDateString: String?,
    val NextStatus: String?,
    val CommentForCashier: String?
)