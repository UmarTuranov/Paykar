package tj.paykar.shop.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import tj.paykar.shop.data.model.wallet.UserInfoModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.model.id.ConfirmLoginModel
import tj.paykar.shop.data.model.id.PaykarIdUserInfoModel
import tj.paykar.shop.data.model.wallet.WalletUserForPaykarModel

class PaykarIdStorage(context: Context) {

    private var pref: SharedPreferences? = context.getSharedPreferences("PAYKAR_ID_DATA", Context.MODE_PRIVATE)
    private val editor = pref?.edit()
    val firstName: String? = pref?.getString("firstName", "")
    val lastName: String? = pref?.getString("lastName", "")
    val phone: String? = pref?.getString("phone", "")
    val userToken: String? = pref?.getString("userToken", "")

    fun saveUser(userInfo: PaykarIdUserInfoModel) {
        editor?.putString("firstName", userInfo.firstName)
        editor?.putString("lastName", userInfo.lastName)
        editor?.putString("phone", userInfo.phone)
        editor?.putString("userToken", userInfo.userToken)
        editor?.apply()
    }

    fun getUser(): PaykarIdUserInfoModel{
        return PaykarIdUserInfoModel(
            userToken,
            firstName,
            lastName,
            phone,
        )
    }

    fun deactivateUser(): Boolean {
        val editor = pref?.edit()
        editor?.clear()
        editor?.apply()
        return true
    }
}