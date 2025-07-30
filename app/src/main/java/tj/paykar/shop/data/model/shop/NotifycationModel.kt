package tj.paykar.shop.data.model.shop

import java.io.Serializable

data class NotifycationModel(
    val createDate: String,
    val title: String,
    val description: String,
    val picture: String,
    val link: String
): Serializable
