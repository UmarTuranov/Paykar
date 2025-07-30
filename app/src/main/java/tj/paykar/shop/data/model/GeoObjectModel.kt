package tj.paykar.shop.data.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class GeoObjectModel(
    @SerializedName("response")
    val response: GeoObjectCollection?
)

data class GeoObjectCollection(
    @SerializedName("GeoObjectCollection")
    val GeoObjectCollection: FeatureMember?
)

data class FeatureMember (
    @SerializedName("featureMember")
    val featureMember: ArrayList<GeoObject>?
)

data class GeoObject (
    val name: String?
)