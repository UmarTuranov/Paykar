package tj.paykar.shop.presentation.shop.qr_scanner

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.dm7.barcodescanner.zxing.ZXingScannerView
import tj.paykar.shop.databinding.ActivityScannerBinding
import tj.paykar.shop.presentation.card.registration.CardRegistrationActivity

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var scannerView: ZXingScannerView? = null
    lateinit var binding: ActivityScannerBinding
    private var isQRCode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)

        isQRCode = intent.extras?.getBoolean("isQRCode") ?: false
        setPermission()
    }

    override fun handleResult(result: com.google.zxing.Result?) {
        val intent = Intent(this@ScannerActivity, CardRegistrationActivity::class.java)
        intent.putExtra("result", result.toString())
        intent.putExtra("isQRCode", isQRCode)
        startActivity(intent)
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

    private fun setPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext, "Необходимо разрешение на использование камеры", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}