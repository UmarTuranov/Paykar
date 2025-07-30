package tj.paykar.shop.data.model.wallet

// USER INFO

data class UserInfoModel(
    val CustomerId: Int?,
    val CustomerCategoryId: Int?,
    val Accounts: ArrayList<Account>?,
    val IdentificationRequest: IdentificationRequestModel,
    val Limit: ArrayList<LimitModel>?,
    val Profile: ProfileModel?,
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestUserInfoModel(
    val CustomerId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class IdentificationRequestModel(
    val RequestState: Int?,
    val RequestStateDesc: String?,
    val Comment: String?
)

data class LimitModel(
    val Category: Int?,
    val AccountType: Int?,
    val Type: Int?,
    val LimitTypeDesc: String?,
    val Daily: Int?,
    val Weekly: Int?,
    val Monthly: Int?,
    val PerOperation: Int?,
    val Account: Int?
)

data class ProfileModel(
    val FirstName: String?,
    val LastName: String?,
    val Gender: String?
)

// USER INFO

// CONFIRM USER
data class ConfirmUserModel(
    val CustomerId: Int?,
    val DeviceInfo: RequestDeviceInfoModel,
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestConfirmUserModel(
    val CustomerId: Int?,
    val ConfirmCode: String?,
    val deviceInfo: RequestDeviceTypeInfoModel,
    val requestInfo: RequestInfoModel
)
// CONFIRM USER

