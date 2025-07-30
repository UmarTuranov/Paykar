package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.AddressModel

interface AddressSelectedManager {
    fun onSelected(address: AddressModel)
}