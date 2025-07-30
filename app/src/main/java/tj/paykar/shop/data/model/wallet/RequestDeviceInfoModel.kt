package tj.paykar.shop.data.model.wallet

data class RequestDeviceTypeInfoModel(
    val Type: Int,
    val Imei: String,
    val AppVersion: String,
    val DeviceModel: String
)

data class RequestDeviceInfoModel(
    val AppVersion: String?,
    val Token: String?,
    val Imei: String?
)