package tj.paykar.shop.data.model.id

data class CheckLoginModel(
    val status: String?,
    val isRegistration: Boolean?,
    val loyalty: CheckLoginLoyaltyModel?,
    val message: String?,
)

data class CheckLoginLoyaltyModel(
    val clientId: Int?,
    val firstName: String?,
    val lastName: String?,
    val birthday: String?
)

/**
"clientId": 85,
"fistName": "Азим",
"lastName": "Каюмов",
"birthday": "2024-07-30T00:00:00"
 */