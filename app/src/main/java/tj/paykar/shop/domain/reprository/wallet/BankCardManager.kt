package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.AddBankCardModel
import tj.paykar.shop.data.model.wallet.BankCardListModel
import tj.paykar.shop.data.model.wallet.DeleteBankCardModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import retrofit2.Response
import tj.paykar.shop.data.model.wallet.BlockCardModel
import tj.paykar.shop.data.model.wallet.EditCardNameModel
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.ResetCardPinCodeModel

interface BankCardManager {
    suspend fun addBankCard(customerId: Int, cardType: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<AddBankCardModel>
    suspend fun deleteBankCard(customerId: Int, cardId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<DeleteBankCardModel>
    suspend fun bankCardList(customerId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<BankCardListModel>
    suspend fun getCardType(customerId: Int, cardNumber: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<PaymentServiceInfoModel>
    suspend fun editCardName(customerId: Int, cardId: Int, cardName: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<EditCardNameModel>
    suspend fun blockCard(customerId: Int, cardId: Int, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<BlockCardModel>
    suspend fun resetPinCode(customerId: Int, cardId: Int, pin: String, deviceInfo: RequestDeviceInfoModel, requestInfo: RequestInfoModel): Response<ResetCardPinCodeModel>
}