package tj.paykar.shop.presentation.wallet.qr

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityMyQrBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject
import tj.paykar.shop.data.REPLENISH_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.data.TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Suppress("DEPRECATION")
class MyQrActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityMyQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityMyQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        setupView()
        zxing()
    }

    private fun setupView() = with(binding) {
        backBtn.setOnClickListener { onBackPressed() }
    }

    private fun zxing() {
        val jsonObject = JSONObject()
        val phone = UserStorage(this).phoneNumber
        jsonObject.put("phone", phone)
        jsonObject.put("serviceId", TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID)
        val jsonObjectString = jsonObject.toString()
        val mWriter = MultiFormatWriter()
        try {
            val mMatrix = mWriter.encode(jsonObjectString, BarcodeFormat.QR_CODE, 400, 400)
            val mEncoder = BarcodeEncoder()
            val mBitmap = mEncoder.createBitmap(mMatrix)
            binding.qrCodeImage.setImageBitmap(mBitmap)
            binding.shareBtn.setOnClickListener {
                shareImage(mBitmap)
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetWorldReadable")
    private fun shareImage(bitmap: Bitmap) {
        try {
            val file = File(externalCacheDir, "Мой QR.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val uri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/*"
            }
            startActivity(Intent.createChooser(intent, "Поделитесь QR-кодом"))

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}