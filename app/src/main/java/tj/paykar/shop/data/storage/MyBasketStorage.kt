package tj.paykar.shop.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.data.model.shop.MyBasketsProductModel
import tj.paykar.shop.data.repository.shop.MyBasketService
import java.lang.reflect.Type

class MyBasketStorage(context: Context): MyBasketService {
    private var pref: SharedPreferences? = context.getSharedPreferences("SHOPPING_LISTS", Context.MODE_PRIVATE)
    private var productLists: ArrayList<MyBasketModel> = arrayListOf()

    override fun createNewBasket(name: String, description: String, product: MyBasketsProductModel?): Boolean {
        val editor = pref?.edit()
        val gSon = Gson()
        val jSon: String = pref?.getString("shopping_products_list", "") ?: ""
        val tYpe: Type = object : TypeToken<ArrayList<MyBasketModel>?>() {}.type
        try {
            productLists = gSon.fromJson(jSon, tYpe)
        }catch (_:Exception){}
        val list = if (product != null){
            MyBasketModel(name, description, arrayListOf(product))
        }else{
            MyBasketModel(name, description, arrayListOf())
        }
        productLists.add(list)
        val gson = Gson()
        val json: String = gson.toJson(productLists)
        editor?.putString("shopping_products_list", json)
        editor?.apply()

        return true
    }

    override fun addProductToTheBasket(product: MyBasketsProductModel, index: Int): Boolean {
        val editor = pref?.edit()
        val gSon = Gson()
        val jSon: String = pref?.getString("shopping_products_list", "") ?: ""
        val tYpe: Type = object : TypeToken<ArrayList<MyBasketModel>?>() {}.type
        try {
            productLists = gSon.fromJson(jSon, tYpe)
            productLists[index].products!!.add(product)
        }catch (e:Exception){
            Log.d("Error While adding product to some basket", e.toString())
        }
        val gson = Gson()
        val json: String = gson.toJson(productLists)
        editor?.putString("shopping_products_list", json)
        editor?.apply()

        return true
    }

    override fun removeProductFromBasket(listIndex: Int, productIndex: Int): Boolean {
        val editor = pref?.edit()
        val gSon = Gson()
        val jSon: String = pref?.getString("shopping_products_list", "") ?: ""
        val tYpe: Type = object : TypeToken<ArrayList<MyBasketModel>?>() {}.type
        productLists = gSon.fromJson(jSon, tYpe)
        try {
            productLists[listIndex].products!!.removeAt(productIndex)
        }catch (e:Exception){
            Log.d("Error While removing product from some basket", e.toString())
        }
        val gson = Gson()
        val json: String = gson.toJson(productLists)
        editor?.putString("shopping_products_list", json)
        editor?.apply()

        return true
    }

    override fun getBasketLists(): ArrayList<MyBasketModel> {
        val gson = Gson()
        val json: String = pref?.getString("shopping_products_list", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<MyBasketModel>?>() {}.type
        return gson.fromJson(json, type) ?: arrayListOf<MyBasketModel>()
    }

    override fun deleteThisBasket(index: Int): Boolean {
        val editor = pref?.edit()
        val gSon = Gson()
        val jSon: String = pref?.getString("shopping_products_list", "") ?: ""
        val tYpe: Type = object : TypeToken<ArrayList<MyBasketModel>?>() {}.type
        try {
            productLists = gSon.fromJson(jSon, tYpe)
        }catch (_:Exception){}
        productLists.removeAt(index)
        val gson = Gson()
        val json: String = gson.toJson(productLists)
        editor?.putString("shopping_products_list", json)
        editor?.apply()
        return true
    }

    override fun removeAllBaskets() {
        val editor = pref?.edit()
        editor?.remove("shopping_products_list")
        editor?.apply()
    }
}