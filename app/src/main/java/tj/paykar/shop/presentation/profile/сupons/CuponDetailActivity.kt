package tj.paykar.shop.presentation.profile.сupons

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityCuponDetailBinding

class CuponDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityCuponDetailBinding

    var qrCodeName: String = ""
    var qrCodeDate: String = ""
    var promoCode: String = ""
    var cuponDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCuponDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        getPutIntent()
        setupView()

    }


    @SuppressLint("SuspiciousIndentation")
    private fun qrCodeGenerated(cardCode: String): Bitmap {
        val width = 220
        val height = 220
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
            try {
                val bitMatrix =
                    codeWriter.encode(cardCode, BarcodeFormat.QR_CODE, width, height)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val color = if (bitMatrix[x, y]) resources.getColor(R.color.green) else Color.WHITE
                        bitmap.setPixel(x, y, color)
                    }
                }
            } catch (e: WriterException) { Log.d("Error", "generateQRCode: ${e.message}") }
        return bitmap

    }

    private fun setupView() {
        binding.apply {
            if (promoCode != "") {
                val qrcode = qrCodeGenerated(promoCode)
                qrCode.setImageBitmap(qrcode)
            }
            couponText.text = "$qrCodeName\n\n$cuponDescription"
            closeButton.setOnClickListener {
                finish()
            }
        }
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        qrCodeName = bundle?.getString("cuponName") ?: ""
        qrCodeDate = bundle?.getString("cuponDate") ?: ""
        promoCode = bundle?.getString("cuponCode") ?: ""
        cuponDescription = bundle?.getString("cuponDescription") ?: ""
    }
}