package tj.paykar.shop.data.repository.shop

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import tj.paykar.shop.data.model.shop.PreferencesModel

interface PreferencesService {
    @GET("api/preferences/list.php")
    suspend fun getPreferencesList(): ArrayList<PreferencesModel>

    @POST("api/preferences/createuserpreferences.php")
    suspend fun savePreferences(@Body requestBody: RequestBody)

    @POST("api/preferences/chosenlist.php")
    suspend fun getUserPreferences(@Body requestBody: RequestBody): ArrayList<PreferencesModel>
}