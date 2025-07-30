package tj.paykar.shop.data.model

data class UserCardStorageModel (
    val clientId: Int,
    val authorized: String?,
    var firstName: String?,
    var lastName: String?,
    val secondName: String?,
    val gender: String?,
    var birthday: String?,
    val phone: String?,
    val token: String?,
    val fToken: String?,
    val balance: String?,
    val cardCode: String?,
    val shortCardCode: String?,
    val lastUpdate: String?,
    val accumulateOnly: Boolean?,
    val IsPhoneConfirmed: Boolean?
)