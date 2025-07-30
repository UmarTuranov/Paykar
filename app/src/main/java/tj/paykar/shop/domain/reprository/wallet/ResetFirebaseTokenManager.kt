package tj.paykar.shop.domain.reprository.wallet

import retrofit2.Response
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.ResetFirebaseTokenModel

interface ResetFirebaseTokenManager {
    suspend fun resetFtoken(customerId: Int, ftoken: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<ResetFirebaseTokenModel>
}