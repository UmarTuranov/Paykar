package tj.paykar.shop.domain.usecase.wallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.domain.reprository.wallet.MainManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainManagerService: MainManager {

    override fun internetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    fun noInternetAlert(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Нет интернета!")
            .setMessage("Соединение с интернетом отсутсвует, пожалуйста включите интернет и попробуйте снова")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun dateConvert(date: String): String {
        return try {
            val localDateTime = LocalDateTime.parse(date)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM uuuu | HH:mm", Locale("ru", "RU"))
            formatter.format(localDateTime)
        } catch (_: Exception) {
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun dateToConvert(date: String): String {
        return try {
            val localDateTime = LocalDateTime.parse(date)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM uuuu")
            formatter.format(localDateTime)
        } catch (_: Exception) {
            ""
        }
    }

    fun convertDate(date: String): String{
        return try {
            val localDate = LocalDateTime.parse(date)
            val day = if (localDate.dayOfMonth < 10) {
                "0${localDate.dayOfMonth}"
            } else {
                "${localDate.dayOfMonth}"
            }
            val month = if (localDate.monthValue < 10) {
                "0${localDate.monthValue}"
            } else {
                "${localDate.monthValue}"
            }
            "$day.$month.${localDate.year}"
        } catch (_: Exception) {
            ""
        }
    }

    override fun dateToLocalDate(date: String): String {
        return ""
    }

    fun toEditable(string: String): Editable {
        return Editable.Factory.getInstance().newEditable(string)
    }

    fun convertDateTime(input: String): String {
        try {
            val instant = Instant.parse(input)
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm")
                .withZone(ZoneId.of("UTC"))
            return formatter.format(instant)
        } catch (e: Exception) {
            Log.d("--E Exception", e.toString())
            return ""
        }
    }

    fun convertDateFormat(date: String?, inputFormat: String, outputFormat: String): String {

        try {
            if (date == null) return ""  // Можете также выбросить исключение или обработать null по-другому

            val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
            val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

            // Парсинг входной даты
            val parsedDate = inputDateFormat.parse(date)  // Теперь безопасно, поскольку date не null

            // Форматирование и возврат в нужном формате
            return outputDateFormat.format(parsedDate ?: "")  // Оператор ?: нужен, если parse вернет null
        } catch (_: Exception) {
            return ""
        }
    }
}