package tj.paykar.shop.data.repository.shop

import retrofit2.http.GET
import tj.paykar.shop.data.model.shop.PromoModel

interface PromoService {

    @GET("bitrix/api/home/promo.php")
    suspend fun getPromo(): ArrayList<PromoModel>
}