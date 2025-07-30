package tj.paykar.shop.data.storage.wallet

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import tj.paykar.shop.data.model.wallet.UserInfoModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.model.wallet.WalletUserForPaykarModel

class IpAddressStorage(context: Context) {

    private var pref: SharedPreferences? = context.getSharedPreferences("IP_ADDRESS_DATA", Context.MODE_PRIVATE)
    private val editor = pref?.edit()
    val ipAddress = pref?.getString("ipAddress", "")

    fun saveIpAddress(ipAddress: String) {
        editor?.putString("ipAddress", ipAddress)
        editor?.apply()
    }
}