package tj.paykar.shop.data.model.shop

import com.google.gson.annotations.SerializedName

data class CatalogSectionModel(

    val sectionId: String?,
    val sectionName: String?,
    val sectionPicture: String?,
    @SerializedName("child")
    val sectionItems: ArrayList<SectionItems>

)

data class SectionItems (

    val id: String?,
    val name: String?,
    val picture: String?,
    @SerializedName("products")
    val sectionProduct: ArrayList<SectionProduct>,

    )

data class SectionProduct (

    val id: String?,
    val name: String?,
    val picture: String?,
    val pictureDetail: String?,
    val price: String?,
    val discountPrice: String?,
    var basket_quan: Double?,
    val wishlist: Boolean?,
    val detailText: String?,
    val hit: String?,
    @SerializedName("baseUnit")
    val unit: String?,
    val nutritional: String?,
    val composition: String?,
    val termConditions: String?,
    val manufacturer: String?,
    val reviews: ArrayList<ProductReviewModel>?
)
