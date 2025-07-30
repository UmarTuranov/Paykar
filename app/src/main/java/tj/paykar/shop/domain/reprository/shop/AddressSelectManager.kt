package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.AddressModel

interface AddressSelectManager {
    fun onSelect(address: AddressModel)
}