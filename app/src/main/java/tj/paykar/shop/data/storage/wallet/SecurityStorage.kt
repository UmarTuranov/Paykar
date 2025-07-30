package tj.paykar.shop.data.storage.wallet

import android.content.Context
import android.content.SharedPreferences

class SecurityStorage(context: Context) {
    private var pref: SharedPreferences? = context.getSharedPreferences("WALLET_SECURITY_DATA", Context.MODE_PRIVATE)
    val fingerPrintEnabled: Boolean? = pref?.getBoolean("fingerPrintEnabled", false)
    val hideBalanceEnabled: Boolean? = pref?.getBoolean("hideBalanceEnabled", false)

    fun saveFingerPrintSetting(enabled: Boolean): Boolean {
        val editor = pref?.edit()
        editor?.putBoolean("fingerPrintEnabled", enabled)
        editor?.apply()
        return true
    }

    fun saveBalanceShow(enabled: Boolean): Boolean {
        val editor = pref?.edit()
        editor?.putBoolean("hideBalanceEnabled", enabled)
        editor?.apply()
        return true
    }

    fun clear(): Boolean {
        val editor = pref?.edit()
        editor?.clear()
        editor?.apply()
        return true
    }
}