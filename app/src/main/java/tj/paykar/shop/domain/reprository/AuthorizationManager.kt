package tj.paykar.shop.domain.reprository

import okhttp3.ResponseBody
import retrofit2.Response
import tj.paykar.shop.data.model.*

interface AuthorizationManager {

    suspend fun sendSmsCode(phone: String): CheckSMSModel

    suspend fun login(phone: String): AuthorizationModel

    suspend fun registration(firstName: String, lastName: String, phone: String): Response<RegistrationModel>

    suspend fun updateUser(userUpdateModel: UserUpdateModel): Response<UserUpdateResultModel>

}