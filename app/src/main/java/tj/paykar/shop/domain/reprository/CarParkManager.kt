package tj.paykar.shop.domain.reprository

import retrofit2.Response
import tj.paykar.shop.data.model.CarParkModel
import tj.paykar.shop.data.model.LastVisitModel

interface CarParkManager {
    suspend fun checkCarNumber(carNumber: String): Response<CarParkModel?>

    suspend fun getLastVisit(userId: Int, plateNumber: String): Response<LastVisitModel>
}