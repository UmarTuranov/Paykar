package tj.paykar.shop.data.model.wallet

data class ExchangeRateModel(
    val usaRate: Currency,
    val euroRate: Currency,
    val russianRate: Currency
)

data class Currency(
    val name: String?,
    val currencyCode: String?,
    val unit: String?,
    val rate: String?
)
