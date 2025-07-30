package tj.paykar.shop.domain.usecase.wallet
import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.util.Base64

class SecurityUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun base64Encode(plainText: String): String {
        val plainTextBytes = plainText.toByteArray(Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(plainTextBytes)
    }

    fun calculateSHA256(input: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val inputBytes = input.toByteArray(Charsets.UTF_8)
        val hashBytes = sha256.digest(inputBytes)

        val stringBuilder = StringBuilder()
        for (byte in hashBytes) {
            stringBuilder.append(String.format("%02x", byte))
        }

        return stringBuilder.toString()
    }
}