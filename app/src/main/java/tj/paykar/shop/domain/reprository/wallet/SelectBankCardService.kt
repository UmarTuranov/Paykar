package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.Account

interface SelectBankCardService {
    fun onSelectCard(isSelected: Boolean, selectedAccount: Account)
}