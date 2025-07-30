package tj.paykar.shop.data.model.shop

data class AddressModel(
    val street: String?,
    val house: String?,
    val entrance: String?,
    val flat: String?,
    val floor: String?,
    var selected: Boolean?
)