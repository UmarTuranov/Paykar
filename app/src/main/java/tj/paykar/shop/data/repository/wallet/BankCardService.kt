package tj.paykar.shop.data.repository.wallet

import okhttp3.RequestBody
import tj.paykar.shop.data.model.wallet.AddBankCardModel
import tj.paykar.shop.data.model.wallet.BankCardListModel
import tj.paykar.shop.data.model.wallet.DeleteBankCardModel
import tj.paykar.shop.data.model.wallet.RequestAddBankCardModel
import tj.paykar.shop.data.model.wallet.RequestBankCardListModel
import tj.paykar.shop.data.model.wallet.RequestDeleteBankCardModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import tj.paykar.shop.data.model.wallet.BlockCardModel
import tj.paykar.shop.data.model.wallet.EditCardNameModel
import tj.paykar.shop.data.model.wallet.PaymentServiceInfoModel
import tj.paykar.shop.data.model.wallet.RequestBlockCardModel
import tj.paykar.shop.data.model.wallet.RequestEditNameCardModel
import tj.paykar.shop.data.model.wallet.RequestResetCardPinCodeModel
import tj.paykar.shop.data.model.wallet.ResetCardPinCodeModel

interface BankCardService {
    @POST("api/card/create")
    suspend fun addBankCard(@Body requestBody: RequestAddBankCardModel, @Header("Sign") sign: String): Response<AddBankCardModel>

    @POST("api/card/delete")
    suspend fun deleteBankCard(@Body requestBody: RequestDeleteBankCardModel, @Header("Sign") sign: String): Response<DeleteBankCardModel>

    @POST("api/cards")
    suspend fun bankCardList(@Body requestBody: RequestBankCardListModel, @Header("Sign") sign: String): Response<BankCardListModel>

    @POST("api/card/info/type")
    suspend fun getCardType(@Body requestBody: RequestBody, @Header("Sign") sign: String): Response<PaymentServiceInfoModel>

    @POST("api/card/name/update")
    suspend fun editCardName(@Body requestBody: RequestEditNameCardModel, @Header("Sign") sign: String): Response<EditCardNameModel>

    @POST("api/card/pin/reset")
    suspend fun resetCardPinCode(@Body requestBody: RequestResetCardPinCodeModel, @Header("Sign") sign: String): Response<ResetCardPinCodeModel>

    @POST("api/card/state/change")
    suspend fun blockCard(@Body requestBody: RequestBlockCardModel, @Header("Sign") sign: String): Response<BlockCardModel>
}