package tj.paykar.shop.data.model.loyalty

import com.google.gson.annotations.SerializedName

data class CuponContentModel(
    val Id: Int,
    val TypeName: String,
    val TypeDisplayName: String,
    val MarketProgramId: Int,
    @SerializedName("ChildrenConentFields")
    val ChildrenConentFields: ArrayList<ChildrenCuponContentModel>
)

data class ChildrenCuponContentModel(
    val Id: Int,
    val TypeDisplayName: String,
    val Value: String
)




