package tj.paykar.shop.data.model.shop

data class MyBasketModel(
    val name: String,
    val description: String,
    val products: ArrayList<MyBasketsProductModel?>?
)
