package tj.paykar.shop.domain.reprository

import retrofit2.Response
import tj.paykar.shop.data.model.FileNameModel
import tj.paykar.shop.data.model.VersionControlModel
import java.io.File

interface ReviewFileManager {
    suspend fun uploadFile(file: File): Response<FileNameModel?>
}