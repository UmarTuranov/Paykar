package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.PaymentCategoryModel
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.PaymentServiceModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import retrofit2.Response

interface PaymentManager {
    suspend fun getPaymentCategory(customerId: Int, categoryId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<PaymentCategoryModel>
    suspend fun getPaymentService(customerId: Int, categoryId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<PaymentServiceModel>
    suspend fun getPaymentServiceInfo(customerId: Int, serviceId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<PaymentServiceInfoModel>
}