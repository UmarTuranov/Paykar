package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.BasketModel

interface BasketEmpty {
    fun basketEmpty(basket: BasketModel): Boolean
}