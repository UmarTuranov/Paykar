package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.AccountOperationHistoryModel
import tj.paykar.shop.data.model.wallet.CheckOperationModel
import tj.paykar.shop.data.model.wallet.CheckQrOperationModel
import tj.paykar.shop.data.model.wallet.ConfirmCreateOperationModel
import tj.paykar.shop.data.model.wallet.CreateOperationModel
import tj.paykar.shop.data.model.wallet.CreateQrOperationModel
import tj.paykar.shop.data.model.wallet.OperationHistoryModel
import tj.paykar.shop.data.model.wallet.OperationInfoModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import retrofit2.Response

interface OperationManager {
    suspend fun getOperationHistory(customerId: Int, startDate: String, endDate: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<OperationHistoryModel>
    suspend fun getOperationInfo(customerId: Int, operationId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<OperationInfoModel>
    suspend fun getAccountOperationHistory(customerId: Int, customerAccount: String, startDate: String, endDate: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<AccountOperationHistoryModel>
    suspend fun checkOperation(customerId: Int, serviceId: Int, account: String, account2: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<CheckOperationModel>
    suspend fun createOperation(customerId: Int, customerAccount: String, serviceId: Int, checkId: Int, account: String, account2: String, comment: String, amount: Double, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<CreateOperationModel>
    suspend fun confirmCreateOperation(customerId: Int, checkId: Int, confirmCode: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<ConfirmCreateOperationModel>
    suspend fun checkQrOperation(customerId: Int, account: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<CheckQrOperationModel>
    suspend fun createQrOperation(customerId: Int, customerAccount: String, checkId: Int, comment: String, amount: Double, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<CreateQrOperationModel>
}