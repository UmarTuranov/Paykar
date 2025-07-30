package tj.paykar.shop.domain.reprository.shop

interface AddPointsRegistrationManager {
    suspend fun addPointsToClient(
        firstName: String,
        lastName: String,
        typeOS: String,
        phoneNumber: String,
        cardCode: String,
        isQrCode: Boolean
    )
}