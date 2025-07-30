package tj.paykar.shop.domain.reprository.shop

import retrofit2.Response
import tj.paykar.shop.data.model.shop.VacationDayModel

interface VacationDayManager {
    suspend fun restDay(): Response<VacationDayModel>
}