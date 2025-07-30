package tj.paykar.shop.data.model.wallet

data class PaymentServiceModel(
    val Services: ArrayList<Services>?
)

data class RequestPaymentServiceModel(
    val CustomerId: Int?,
    val CategoryId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class Services(
    val ServiceId: Int?,
    val ServiceName: String?,
    val Icon: String?
)

// PAYMENT SERVICE INFO
data class PaymentServiceInfoModel(
    val Id: Int?,
    val Name: String?,
    val Currency: String?,
    val MinAmount: Float?,
    val MaxAmount: Float?,
    val Icon: String?,
    val CommissionProfiles:ArrayList<CommissionProfiles>?,
    val ServiceParameters: ArrayList<ServiceParameters>?,
    val LastAccounts: ArrayList<String>?,
    val LastAmounts: ArrayList<Double>?,
    val rateService: RateServiceModel?,
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RateServiceModel(
    val CurrencyCode: String?,
    val RateValue: Double?
)

data class CommissionProfiles(
    val AccountType: Int?,
    val CommissionDetails: CommissionDetails
)

data class CommissionDetails(
    val Name: String?,
    val Type: String?,
    val Rate: Double?,
    val MinAmount: Float?,
    val MaxAmount: Int?,
    val FixAmount: Int?
)

data class ServiceParameters(
    val ParamName: String?,
    val ParamProperties: ParamProperties
)

data class ParamProperties(
    val SerialNumber: Int?,
    val ParamDescription: String?,
    val Message: String?,
    val Regex: String?,
    val Type: String?,
    val MaxLength: Int?
)

data class RequestPaymentServiceInfoModel(
    val CustomerId: Int?,
    val ServiceId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)
// PAYMENT SERVICE INFO