package tj.paykar.shop.domain.reprository.wallet

import tj.paykar.shop.data.model.wallet.AuthorizationModel
import tj.paykar.shop.data.model.wallet.ConfirmUserModel
import tj.paykar.shop.data.model.wallet.RegistrationModel
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestDeviceTypeInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.model.wallet.UserInfoModel
import retrofit2.Response
import tj.paykar.shop.data.model.wallet.WalletUserDataForPaykarModel
import tj.paykar.shop.data.model.wallet.WalletUserForPaykarModel

interface UserManager {
    suspend fun createUser(
        login: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<RegistrationModel>

    suspend fun login(login: String,
                      deviceInfo: RequestDeviceTypeInfoModel,
                      requestInfoModel: RequestInfoModel
    ): Response<AuthorizationModel>

    suspend fun getUserInfo(
        customerId: Int,
        deviceInfo: RequestDeviceInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<UserInfoModel>

    suspend fun confirmRegistrationUser(
        customerId: Int,
        confirmCode: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<ConfirmUserModel>

    suspend fun confirmLoginUser(
        customerId: Int,
        confirmCode: String,
        deviceInfo: RequestDeviceTypeInfoModel,
        requestInfoModel: RequestInfoModel
    ): Response<ConfirmUserModel>

    suspend fun createWalletUser(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        birthDate: String,
        deviceModel: String,
        versionOS: String,
        ftoken: String,
        imei: String,
        ipAddress: String,
        walletAppVersion: String,
        shopUserId: Int
    ): Response<WalletUserForPaykarModel>

    suspend fun walletUserInfoPaykar(
        phone: String
    ): Response<WalletUserForPaykarModel>

    suspend fun editWalletUserPaykar(
        walletUserId: Int,
        firstName: String,
        lastName: String,
        gender: String,
        phoneNumber: String,
        birthDate: String,
        balance: String,
        deviceModel: String,
        typeOS: String,
        versionOS: String,
        ftoken: String,
        walletAppVersion: String,
        imei: String,
        ipAddress: String,
        shopUserId: Int
    ): Response<WalletUserForPaykarModel>

    suspend fun updateWalletUserPaykar(
        walletUserId: Int,
        deviceModel: String,
        versionOS: String,
        ftoken: String,
        imei: String,
        ipAddress: String,
        walletAppVersion: String,
        balance: String
    ): Response<WalletUserForPaykarModel>
}