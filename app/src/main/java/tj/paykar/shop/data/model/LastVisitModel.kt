package tj.paykar.shop.data.model

data class LastVisitModel(
    val id: Int?,
    val tariffName: String?,
    val status: String?,
    val entryTime: String?,
    val exitTime: String?,
    val totalTime: String?,
    val plateNumber: String?,
    val paid: String?
)
