package tj.paykar.shop.data.repository

import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.model.UserUpdateModel

interface UserStorage {

    fun getUser(): UserStorageModel

    fun saveUser(userStorage: UserStorageModel): Boolean

    fun updateUser(userUpdateModel: UserUpdateModel): Boolean

    fun getYandexMap(): Boolean

    fun saveYandexMap(openMap: Boolean): Boolean

}