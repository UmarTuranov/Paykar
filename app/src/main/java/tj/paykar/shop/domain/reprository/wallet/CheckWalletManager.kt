package tj.paykar.shop.domain.reprository.wallet

import retrofit2.Response
import tj.paykar.shop.data.model.wallet.CheckWalletModel
import tj.paykar.shop.data.model.wallet.ContactModel

interface CheckWalletManager {
    suspend fun checkWallet(ptoken: String, contacts: ArrayList<ContactModel>): Response<CheckWalletModel>
}