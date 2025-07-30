package tj.paykar.shop.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.model.home.StoriesModel
import java.lang.reflect.Type

class StoriesStorage(context: Context) {
    private var pref: SharedPreferences? = context.getSharedPreferences("STORIES", Context.MODE_PRIVATE)
    private var seenList: ArrayList<Int> = arrayListOf()

    fun saveStories(stories: ArrayList<StoriesModel>) {
        val editor = pref?.edit()
        val gson = Gson()
        val json: String = gson.toJson(stories)
        editor?.putString("stories", json)
        editor?.apply()
    }

    fun getStories(): ArrayList<StoriesModel> {
        val gson = Gson()
        val json: String = pref?.getString("stories", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<StoriesModel>>() {}.type
        return try {
            gson.fromJson(json, type)
        }catch (_:Exception){
            arrayListOf()
        }
    }

    fun addSeen(id: Int){
        val gson = Gson()
        val json: String = pref?.getString("seen", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<Int>>() {}.type
        seenList = try {
            gson.fromJson(json, type)
        }catch (_:Exception){
            arrayListOf()
        }
        seenList.add(id)

        val editor = pref?.edit()
        val seen: String = gson.toJson(seenList)
        editor?.putString("seen", seen)
        editor?.apply()
    }

    fun getSeen(): ArrayList<Int>{
        val gson = Gson()
        val json: String = pref?.getString("seen", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<Int>>() {}.type
        return try {
            gson.fromJson(json, type)
        }catch (_:Exception){
            arrayListOf()
        }
    }
}