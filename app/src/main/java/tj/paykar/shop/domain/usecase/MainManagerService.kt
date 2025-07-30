package tj.paykar.shop.domain.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import tj.paykar.shop.domain.reprository.MainManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun dateConvert(date: String): String {
        return try {
            val localDateTime = LocalDateTime.parse(date)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM uuuu | HH:mm")
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

    override fun dateToLocalDate(date: String): String {
        return ""
    }


}