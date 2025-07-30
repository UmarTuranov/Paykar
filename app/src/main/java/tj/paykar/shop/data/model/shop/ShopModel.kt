package tj.paykar.shop.data.model.shop

import tj.paykar.shop.data.model.ReviewModel

data class ShopModel(
    val catalogList: ArrayList<CatalogModel>,
    val profitablyList: ArrayList<CatalogSectionProductModel>,
    val promoList: ArrayList<PromoModel>,
    val reviewList: ArrayList<ReviewModel>,
    val recomendationList: ArrayList<RecommendationProductModel>,
    val basketCount: Int
)