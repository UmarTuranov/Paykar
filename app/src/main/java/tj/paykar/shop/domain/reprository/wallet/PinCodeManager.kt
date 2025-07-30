package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.PinCodeModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import retrofit2.Response

interface PinCodeManager {
    suspend fun setPinCode(customerId: Int,
                           pinCode: String,
                           deviceInfo: RequestDeviceInfoModel,
                           requestInfo: RequestInfoModel
    ): Response<PinCodeModel>
    suspend fun resetPinCode(customerId: Int,
                             oldPinCode: String,
                             newPinCode: String,
                             deviceInfo: RequestDeviceInfoModel,
                             requestInfo: RequestInfoModel
    ): Response<PinCodeModel>
    suspend fun checkPinCode(customerId: Int,
                             pinCode: String,
                             deviceInfo: RequestDeviceInfoModel,
                             requestInfo: RequestInfoModel
    ): Response<PinCodeModel>
    suspend fun forgetPinCode(customerId: Int,
                              deviceInfo: RequestDeviceInfoModel,
                              requestInfo: RequestInfoModel
    ): Response<PinCodeModel>
    suspend fun confirmForgetPinCode(customerId: Int,
                                     confirmCode: String,
                                     deviceInfo: RequestDeviceInfoModel,
                                     requestInfo: RequestInfoModel
    ): Response<PinCodeModel>
}