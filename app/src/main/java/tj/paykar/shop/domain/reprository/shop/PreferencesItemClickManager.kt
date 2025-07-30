package tj.paykar.shop.domain.reprository.shop

import tj.paykar.shop.data.model.shop.PreferencesModel

interface PreferencesItemClickManager {
    fun checkSelected(preferences: ArrayList<PreferencesModel>)
}