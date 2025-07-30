package tj.paykar.shop.data.repository.wallet

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tj.paykar.shop.data.model.wallet.CheckWalletModel

interface CheckWalletService {
    @POST("api/contact/check_wallet.php")
    suspend fun checkWallet(@Body requestBody: RequestBody): Response<CheckWalletModel>
}