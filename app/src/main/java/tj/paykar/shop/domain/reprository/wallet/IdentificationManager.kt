package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.IdentificationModel
import tj.paykar.shop.data.model.wallet.IdentificationPerson
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import retrofit2.Response

interface IdentificationManager {
    suspend fun identify(customerId: Int,
                         personal: IdentificationPerson,
                         deviceInfo: RequestDeviceInfoModel,
                         requestInfo: RequestInfoModel
    ): Response<IdentificationModel>
}