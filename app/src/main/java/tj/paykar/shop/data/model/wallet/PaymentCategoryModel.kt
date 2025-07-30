package tj.paykar.shop.data.model.wallet

data class PaymentCategoryModel(
    val ServiceCategories: ArrayList<PaymentCategory>?,
)

data class RequestPaymentCategoryModel(
    val CustomerId: Int?,
    val CategoryId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class PaymentCategory(
    val CategoryId: Int?,
    val CategoryName: String?,
    val HasCategory: Boolean?,
    val Icon: String?
)