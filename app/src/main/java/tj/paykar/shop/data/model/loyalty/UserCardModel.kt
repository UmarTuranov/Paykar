package tj.paykar.shop.data.model.loyalty

data class UserCardModel (
    val Client: ClientCard,
    val Token: String
)

data class ClientCard (
    val AccountId: Int,
    val FullName: String,
    val Gender: String,
    val Birthday: String,
    val PhoneMobile: String,
    val Email: String,
    val Balance: Double,
    val Cards: ArrayList<Cards>
)

data class Cards(
    val CardId: Int,
    val AccountId: Int,
    val CardCode: String,
    val Blocked: Boolean,
    val BlockedString: String,
    val AccumulateOnly: Boolean,
    val AccumulateOnlyString: String,
    val StartDate: String,
    val FinishDate: String,
    val BonusProgramName: String,
    val IsSuspicious: Boolean,
    val CreateByMarketProgramId: Int
)
