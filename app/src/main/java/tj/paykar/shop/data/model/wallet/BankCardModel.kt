package tj.paykar.shop.data.model.wallet

// ADD BANK CARD MODELS
data class RequestAddBankCardModel(
    val CustomerId: Int?,
    val CardType: Int?,
    val DeviceInfo: RequestDeviceInfoModel,
    val RequestInfo: RequestInfoModel
)

data class AddBankCardModel(
    val ConfirmUrl: String?,
    val ResultCode: Int?,
    val ResultDesc: String?
)
// ADD BANK CARD MODELS


// DELETE BANK CARD MODELS
data class DeleteBankCardModel(
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestDeleteBankCardModel(
    val CustomerId: Int?,
    val CardId: Int?,
    val DeviceInfo: RequestDeviceInfoModel,
    val RequestInfo: RequestInfoModel
)
// DELETE BANK CARD MODELS

//BANK CARD LIST MODELS
data class BankCardListModel(
    val Cards: ArrayList<BankCard>?,
    val ResultCode: Int?,
    val ResultDesc: String?
)

data class RequestBankCardListModel(
    val CustomerId: Int?,
    val deviceInfo: RequestDeviceInfoModel,
    val requestInfo: RequestInfoModel
)

data class BankCard(
    val Id: Int?,
    val Account: Account,
    val Type: Int?,
    val Number: String?,
    val ExpDate: String?,
    val Name: String?,
    val IsLocalCard: Boolean?
)

data class Account(
    val Id: Int?,
    val Account: String?,
    val Type: Int?,
    val Currency: String?,
    val Balance: Double?,
    val AccountName: String?,
    val ShowBalance: Boolean?
)
//BANK CARD LIST MODELS