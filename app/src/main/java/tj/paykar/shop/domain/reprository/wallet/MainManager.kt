package tj.paykar.shop.domain.reprository.wallet

import android.content.Context

interface MainManager {
    fun internetConnection(context: Context): Boolean

    fun dateConvert(date: String): String

    fun dateToConvert(date: String): String

    fun dateToLocalDate(date: String): String
}