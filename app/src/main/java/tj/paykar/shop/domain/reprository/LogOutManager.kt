package tj.paykar.shop.domain.reprository

interface LogOutManager {
    suspend fun logOut(phone: String, deviceModel: String)
}