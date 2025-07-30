package tj.paykar.shop.data.model.loyalty

import com.google.gson.annotations.SerializedName

data class HistoryCardModel(
    val DocumentId: Int,
    val DocumentDate: String,
    val CreateDate: String,
    val SubjectFullAdress: String,
    val DocumentCode: String,
    val DocumentFiscalCode: String,
    val TotalSum: Double,
    val TotalSumDiscounted: Double,
    val AddBonus: Double,
    val RemoveBonus: Double,
    val Discount: Double,
    val DiscountPercent: Double,
    val CashName: String,
    val CashierName: String,
    val AddChips: Int,
    val RemoveChips: Int,
    val PaymentForm: String,
    @SerializedName("DocumentDetails")
    val products: ArrayList<DocumentDetails>

)

data class DocumentDetails (
    val ProductCode: String,
    val ProductName: String,
    val Quantity: Double,
    val TotalPrice: Double,
    val TotalPriceDiscounted: Double,
    val AddBonus: Double,
    val RemoveBonus: Double
)
