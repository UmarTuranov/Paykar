package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.PreferencesModel

interface PreferencesManager {
    suspend fun getPreferencesList(): ArrayList<PreferencesModel>

    suspend fun savePreferences(userId: Int, preferences: ArrayList<PreferencesModel>)

    suspend fun getUserPreferences(userId: Int): ArrayList<PreferencesModel>
}