package tj.paykar.shop.data.storage.wallet

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import tj.paykar.shop.data.model.wallet.UserInfoModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.model.wallet.WalletUserForPaykarModel

class UserStorage(context: Context) {

    private var pref: SharedPreferences? = context.getSharedPreferences("WALLET_USER_DATA", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val editor = pref?.edit()
    val customerId: Int? = pref?.getInt("customerId", 0)
    val token: String? = pref?.getString("token", "")
    val phoneNumber: String? = pref?.getString("phoneNumber", "")
    val showingIdentificationCardCount: Int = pref?.getInt("identificationCardCount", 0) ?: 0
    private val userInfo: String? = pref?.getString("userInfoModel", "")
    val paykarUserInfo: String? = pref?.getString("paykarUserModel", "")
    val pinCodeEntered: Boolean? = pref?.getBoolean("pinCodeEntered", false)
    val isRegistration: Boolean? = pref?.getBoolean("isRegistration", false)
    val walletIsActive: Boolean? = pref?.getBoolean("walletIsActive", false)

    fun saveUser(customerId: Int, phoneNumber: String, token: String, isRegistration: Boolean = false): Boolean {
        editor?.putInt("customerId", customerId)
        editor?.putString("token", token)
        editor?.putString("phoneNumber", phoneNumber)
        editor?.putBoolean("isRegistration", isRegistration)
        editor?.apply()
        return true
    }

    fun saveIsRegistration(isRegistration: Boolean) {
        editor?.putBoolean("isRegistration", isRegistration)
        editor?.apply()
    }

    fun savePaykarUserInfo(userInfo: WalletUserForPaykarModel): Boolean {
        val json = gson.toJson(userInfo)
        editor?.putString("paykarUserModel", json)
        editor?.apply()
        return true
    }

    fun getPaykarUserInfo(): WalletUserForPaykarModel? {
        Log.d("--I paykarUserInfo", paykarUserInfo.toString())
        return if (paykarUserInfo != null) {
            val type = object : TypeToken<WalletUserForPaykarModel>() {}.type
            gson.fromJson<WalletUserForPaykarModel>(paykarUserInfo, type)
        } else {
            null
        }
    }

    fun saveUserInfo(userInfoModel: UserInfoModel): Boolean {
        val json = gson.toJson(userInfoModel)
        editor?.putString("userInfoModel", json)
        editor?.apply()
        return true
    }

    fun getUserInfo(): UserInfoModel? {
        return if (userInfo != null) {
            val type = object : TypeToken<UserInfoModel>() {}.type
            gson.fromJson<UserInfoModel>(userInfo, type)
        } else {
            null
        }
    }

    fun saveShowingIdentificationCardCount() {
        if (shouldShowIdentificationCard()) {
            editor?.putInt("identificationCardCount", showingIdentificationCardCount.plus(1))
            editor?.apply()
        }
    }

    fun shouldShowIdentificationCard(): Boolean {
        Log.d("--C Count", showingIdentificationCardCount.toString())
        return showingIdentificationCardCount <= 3
    }

    fun savePinCodeEntered(isEntered: Boolean) {
        editor?.putBoolean("pinCodeEntered", isEntered)
        editor?.apply()
    }

    fun saveWalletIsActive(isActive: Boolean) {
        editor?.putBoolean("walletIsActive", isActive)
        editor?.apply()
    }

    fun deactivateUser(): Boolean {
        val editor = pref?.edit()
        editor?.clear()
        editor?.apply()
        return true
    }
}