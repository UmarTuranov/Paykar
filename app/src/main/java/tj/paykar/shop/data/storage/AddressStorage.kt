package tj.paykar.shop.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.paykar.shop.data.model.shop.AddressModel
import tj.paykar.shop.data.repository.shop.AddressService
import java.lang.reflect.Type

class AddressStorage(context: Context): AddressService {
    private var pref: SharedPreferences? = context.getSharedPreferences("ADDRESS", Context.MODE_PRIVATE)
    private var addressList: ArrayList<AddressModel> = arrayListOf()

    override fun saveAddress(address: AddressModel): Boolean {
        val editor = pref?.edit()
        val gSon = Gson()
        val jSon: String = pref?.getString("address", "") ?: ""
        val tYpe: Type = object : TypeToken<ArrayList<AddressModel>?>() {}.type
        try {
            addressList = gSon.fromJson(jSon, tYpe)
            for (i in 0 until addressList.size){
                addressList[i].selected = false
            }
        }catch (_:Exception){}
        addressList.add(address)
        val gson = Gson()
        val json: String = gson.toJson(addressList)
        editor?.putString("address", json)
        editor?.apply()

        return true
    }

    override fun saveAddressList(listOfAddresses: ArrayList<AddressModel>): Boolean {
        val editor = pref?.edit()
        val gson = Gson()
        val json: String = gson.toJson(listOfAddresses)
        editor?.putString("address", json)
        editor?.apply()
        return true
    }

    override fun getAddressList(): ArrayList<AddressModel>? {
        val gson = Gson()
        val json: String = pref?.getString("address", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<AddressModel>?>() {}.type
        return gson.fromJson(json, type)
    }
}