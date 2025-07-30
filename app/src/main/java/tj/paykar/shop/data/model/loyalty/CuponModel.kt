package tj.paykar.shop.data.model.loyalty

data class CuponModel(
    val programName: String,
    val programDescription: String,
    val previewImage: String,
    val dateStart: String,
    val dateFinish: String,
    val barcode: String
)
