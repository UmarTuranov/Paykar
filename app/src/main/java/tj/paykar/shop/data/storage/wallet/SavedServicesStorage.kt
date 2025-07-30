package tj.paykar.shop.data.storage.wallet

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class SavedServicesStorage(context: Context) {
    private val pref: SharedPreferences? = context.getSharedPreferences("WALLET_SAVED_SERVICES", Context.MODE_PRIVATE)
    private val editor = pref?.edit()
    private val services: String? = pref?.getString("services", "")

    fun savedService(serviceName: String, serviceId: Int, serviceAccount: String, serviceAccount2: String, paymentSum: Double, icon: String) {
        val jsonArray = if (!services.isNullOrEmpty()) {
            JSONArray(services)
        } else {
            JSONArray()
        }

        val jsonObject = JSONObject()
        jsonObject.put("serviceName", serviceName)
        jsonObject.put("serviceId", serviceId)
        jsonObject.put("serviceAccount", serviceAccount)
        jsonObject.put("serviceAccount2", serviceAccount2)
        jsonObject.put("paymentSum", paymentSum)
        jsonObject.put("icon", icon)

        jsonArray.put(jsonObject)
        editor?.putString("services", jsonArray.toString())
        editor?.apply()
    }

    fun getSavedServices(): ArrayList<HashMap<String, Any>> {
        val servicesList = ArrayList<HashMap<String, Any>>()
        if (!services.isNullOrEmpty()) {
            val jsonArray = JSONArray(services)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val serviceData = HashMap<String, Any>()
                serviceData["serviceName"] = jsonObject.getString("serviceName")
                serviceData["serviceId"] = jsonObject.getInt("serviceId")
                serviceData["serviceAccount"] = jsonObject.getString("serviceAccount")
                serviceData["serviceAccount2"] = jsonObject.getString("serviceAccount2")
                serviceData["paymentSum"] = jsonObject.getDouble("paymentSum")
                serviceData["icon"] = jsonObject.getString("icon")
                servicesList.add(serviceData)
            }
        }
        return servicesList
    }

    fun deleteService(serviceId: Int, serviceAccount: String) {
        if (services.isNullOrEmpty()) {
            return
        }
        val jsonArray = JSONArray(services)
        val newArray = JSONArray()
        var isDeleted = false
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            Log.d("--J Json", jsonObject.toString())
            if (jsonObject.getInt("serviceId") == serviceId &&
                jsonObject.getString("serviceAccount") == serviceAccount) {
                isDeleted = true
                continue
            }
            newArray.put(jsonObject)
        }

        if (isDeleted) {
            editor?.putString("services", newArray.toString())
            editor?.apply()
        }
    }

    fun existingService(serviceId: Int, account: String): Boolean {
        var existing = false
        if (services.isNullOrEmpty()) {
            return false
        }
        val jsonArray = JSONArray(services)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (jsonObject.getString("serviceAccount") == account && jsonObject.getInt("serviceId") == serviceId) {
                existing = true
                break
            }
        }

        return existing
    }

    fun clearSavedServices() {
        editor?.clear()?.apply()
    }
}
