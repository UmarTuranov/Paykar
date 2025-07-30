package tj.paykar.shop.presentation.wallet.qr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import tj.paykar.shop.data.QR_PAYMENT_ID
import tj.paykar.shop.data.TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.presentation.wallet.pincode.CodeActivity

class QrScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var scannerView: ZXingScannerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        openCamera(1010)
    }

    override fun handleResult(res: Result?) {
        val result = res.toString()
        Log.d("--R Result", result)
        try {
            val jsonObject: JsonObject = JsonParser.parseString(result).asJsonObject
            val phone = jsonObject.get("phone").asString
            val serviceId = jsonObject.get("serviceId").asInt
            if (serviceId == TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID) {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("activityType", "savedService")
                intent.putExtra("savedServiceAccount", phone)
                intent.putExtra("serviceId", serviceId)
                startActivity(intent)
            } else {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("result", result)
                intent.putExtra("activityType", "qrPayment")
                intent.putExtra("serviceId", QR_PAYMENT_ID)
                startActivity(intent)
            }
        } catch (e: Exception) {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("result", result)
            intent.putExtra("activityType", "qrPayment")
            intent.putExtra("serviceId", QR_PAYMENT_ID)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
    }

    private fun openCamera(requestCode: Int) {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (cameraPermission == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                showPermissionExplanation("Доступ к камере необходим для съемки фотографий") {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        requestCode
                    )
                }
            } else {
                if (!hasUserDeniedForever(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        requestCode
                    )
                } else {
                    redirectToAppSettings("Включите доступ к камере в настройках")
                }
            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && storagePermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                showPermissionExplanation("Доступ к памяти необходим для сохранения фотографий") {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCode
                    )
                }
            } else {
                if (!hasUserDeniedForever(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCode
                    )
                } else {
                    redirectToAppSettings("Включите доступ к памяти в настройках")
                }
            }
        }
    }

    private fun hasUserDeniedForever(permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
    }

    private fun redirectToAppSettings(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Камера недоступна")
            .setMessage(message)
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss(); finish()}
            .show()
    }

    private fun showPermissionExplanation(message: String, onProceed: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("Разрешить") { _, _ -> onProceed() }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }

    }
}