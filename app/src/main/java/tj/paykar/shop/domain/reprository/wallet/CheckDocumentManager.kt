package tj.paykar.shop.domain.reprository.wallet

import retrofit2.Response
import tj.paykar.shop.data.model.wallet.CheckDocumentModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel

interface CheckDocumentManager {
    suspend fun checkDocument(customerId: Int, taxId: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<CheckDocumentModel>
}