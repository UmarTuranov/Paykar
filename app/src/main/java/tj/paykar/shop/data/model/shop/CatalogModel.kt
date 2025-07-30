package tj.paykar.shop.data.model.shop

import com.google.gson.annotations.SerializedName

data class CatalogModel(
    val sectionId: String?,
    val sectionName: String?,
    val sectionPicture: String?,
    @SerializedName("child")
    val sectionList: ArrayList<Sections>
)

data class Sections (
    val id: String?,
    val name: String?,
    val picture: String?
)
