package tj.paykar.shop.data.model.shop

data class PreferencesModel(
    val id: Int,
    val name: String,
    var selected: Boolean?
)
