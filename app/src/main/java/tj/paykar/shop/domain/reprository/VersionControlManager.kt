package tj.paykar.shop.domain.reprository

import retrofit2.Response
import tj.paykar.shop.data.model.VersionControlModel

interface VersionControlManager {
    suspend fun checkVersion(version: String): Response<VersionControlModel?>
}