package tj.paykar.shop.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CarNumberStorage(context: Context) {
    private var pref: SharedPreferences? = context.getSharedPreferences("CAR_NUMBER", Context.MODE_PRIVATE)
    private var carsNumbersList: MutableList<String> = mutableListOf()

    fun saveCarNumber(number: String){
        val editor = pref?.edit()
        val gSon = Gson()
        val jSon: String = pref?.getString("carsNumbersList", "") ?: ""
        val tYpe: Type = object : TypeToken<MutableList<String>>() {}.type
        try {
            carsNumbersList = gSon.fromJson(jSon, tYpe)
        }catch (_:Exception){}
        if (number !in carsNumbersList){
            carsNumbersList.add(number)
        }
        val gson = Gson()
        val json: String = gson.toJson(carsNumbersList)
        editor?.putString("carsNumbersList", json)
        editor?.apply()
    }

    fun getCarNumberList(): MutableList<String> {
        return try {
            val gson = Gson()
            val json: String = pref?.getString("carsNumbersList", "") ?: ""
            val type: Type = object : TypeToken<MutableList<String>>() {}.type
            gson.fromJson(json, type)
        }catch (_:Exception){
            mutableListOf()
        }
    }
}