package tj.paykar.shop.domain.reprository.id

import com.google.gson.JsonObject
import retrofit2.Response
import tj.paykar.shop.data.model.id.AutoRegistrationModel
import tj.paykar.shop.data.model.id.CheckLoginModel
import tj.paykar.shop.data.model.id.ConfirmLoginModel
import tj.paykar.shop.data.model.id.UpdateUserModel

interface PaykarIdManager {

    suspend fun checkLogin(phone: String): Response<CheckLoginModel>

    suspend fun confirmLogin(
        phone: String,
        firstName: String,
        lastName: String,
        deviceModel: String,
        typeOS: String = "Android",
        versionOS: String,
        versionApp: String,
        imei: String,
        macaddress: String = "",
        ipaddress: String,
        ftoken: String,
        confirmCode: String,
    ): Response<ConfirmLoginModel>

    suspend fun autoRegistration(
        phone: String,
        firstName: String,
        lastName: String,
        deviceModel: String,
        typeOS: String = "Android",
        versionOS: String,
        versionApp: String,
        imei: String,
        macAddress: String,
        ipAddress: String,
        ftoken: String,
        ltoken: String
    ): Response<AutoRegistrationModel>

    suspend fun updateUser(
        ptoken: String,
        ftoken: String,
        ltoken: String,
        deviceModel: String,
        versionApp: String,
        versionOS: String,
        imei: String,
        macaddress: String,
        ipaddress: String
    ): UpdateUserModel?
}

/*
UpdateUser
$ptoken = $postData["ptoken"];
$ftoken = $postData["ftoken"];
$ltoken = $postData["ltoken"];
$deviceModel = $postData["deviceModel"];
$versionApp = $postData["versionApp"];
$versionOS = $postData["versionOS"];
$imei = $postData["imei"];
$macAddress = $postData["macaddress"];
$ipAddress = $postData["ipaddress"];
 */

/*
AutoRegistration
        $firstName = $postData["firstName"];
        $lastName = $postData["lastName"];
        $deviceModel = $postData["deviceModel"];
        $typeOS = $postData["typeOS"];
        $versionOS = $postData["versionOS"];
        $versionApp = $postData["versionApp"];
        $imei = $postData["imei"];
        $macAddress = $postData["macAddress"];
        $ipAddress = $postData["ipAddress"];
        $ftoken = $postData["ftoken"];
        $loyaltyToken = $postData['ltoken'];
 */

/*
ConfirmLogin
        $firstName = $postData["firstName"];
        $lastName = $postData["lastName"];
        $deviceModel = $postData["deviceModel"];
        $typeOS = $postData["typeOS"];
        $versionOS = $postData["versionOS"];
        $versionApp = $postData["versionApp"];
        $imei = $postData["imei"];
        $macAddress = $postData["macaddress"];
        $ipAddress = $postData["ipaddress"];
        $ftoken = $postData["ftoken"];
        $confirmCode = $postData["confirmCode"];
 */