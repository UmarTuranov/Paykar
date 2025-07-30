package tj.paykar.shop.data.storage

import android.content.Context
import android.content.SharedPreferences

class NullableHaveStorage(context: Context) {
    private var pref: SharedPreferences? = context.getSharedPreferences("NULLABLE", Context.MODE_PRIVATE)

    fun setHaveNullable(){
        val editor = pref?.edit()
        editor?.putString("nullHave", "yes")
        editor?.apply()
    }

    fun getHaveNullable(): String{
        return pref?.getString("nullHave", "") ?: ""
    }

    fun removeNullableHave(){
        val editor = pref?.edit()
        editor?.remove("nullHave")
        editor?.apply()
    }
}