package tj.paykar.shop.data.repository.shop

import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.data.model.shop.MyBasketsProductModel

interface MyBasketService {
    fun createNewBasket(name: String, description: String, product: MyBasketsProductModel?): Boolean
    fun addProductToTheBasket(product: MyBasketsProductModel, index: Int): Boolean
    fun removeProductFromBasket(listIndex: Int, productIndex: Int): Boolean
    fun getBasketLists(): ArrayList<MyBasketModel>
    fun deleteThisBasket(index: Int): Boolean
    fun removeAllBaskets()
}