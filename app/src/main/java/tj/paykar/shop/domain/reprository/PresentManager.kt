package tj.paykar.shop.domain.reprository

import retrofit2.Response

interface PresentManager {

    suspend fun slideItems(): Response<ArrayList<String>>

}