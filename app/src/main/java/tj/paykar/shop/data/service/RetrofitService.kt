package tj.paykar.shop.data.service

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitService {

    private val baseUrl = "https://paykar.shop/"
    private val baseYandexUrl = "https://geocode-maps.yandex.ru/"
    private val serverUrl = "https://mobileapp.paykar.tj/"
    private val processingUrl = "https://paykar.cloud39.ru/BonusWebApi/"
    private val officialSite = "https://paykar.tj/"
    private val parkingUrl = "https://pms.paykar.tj"
    private val paykarJobUrl = "https://job.paykar.tj/"
    private val paykarAdminUrl = "https://admin.paykar.tj"
    private val ipAddressUrl = "https://api.ipify.org/"
    private val paykarBitrixUrl = "https://paykar24.tj/"
    private val paykarIdUrl = "https://test.paykar.tj/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun ipAddressRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ipAddressUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun paykarBitrixRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(paykarBitrixUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun paykarIdRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(paykarIdUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun paykarJobRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(paykarJobUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun paykarAdminRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(paykarAdminUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun sendRequest (): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit

    }

    fun yandexRequest (): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseYandexUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit

    }

    fun serverRequest (): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit

    }

    fun processing(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(processingUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun siteRequest(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(officialSite)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun parkingRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(parkingUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}