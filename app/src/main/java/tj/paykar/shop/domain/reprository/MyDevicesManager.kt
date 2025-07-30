package tj.paykar.shop.domain.reprository

import tj.paykar.shop.data.model.MyDevicesModel

interface MyDevicesManager {
    suspend fun getDevices(phone: String): ArrayList<MyDevicesModel>
}