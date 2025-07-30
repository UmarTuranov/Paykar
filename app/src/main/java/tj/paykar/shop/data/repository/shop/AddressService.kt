package tj.paykar.shop.data.repository.shop

import tj.paykar.shop.data.model.shop.AddressModel

interface AddressService {
    fun saveAddress(address: AddressModel): Boolean
    fun saveAddressList(listOfAddresses: ArrayList<AddressModel>): Boolean
    fun getAddressList(): ArrayList<AddressModel>?
}