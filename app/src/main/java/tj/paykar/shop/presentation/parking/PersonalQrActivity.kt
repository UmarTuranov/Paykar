package tj.paykar.shop.presentation.parking

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityPersonalQrBinding

class PersonalQrActivity : AppCompatActivity() {
    lateinit var binding: ActivityPersonalQrBinding
    private var timer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val bundle: Bundle? = intent.extras
        val qrCode = bundle?.getString("qrCode") ?: ""
        val limitDate = bundle?.getString("limitDate") ?: ""
        val (h,m,s) = limitDate.split(":")
        val totalTime = h.toInt() * 3600000 + m.toInt() * 60000 + s.toInt() * 1000
        if (totalTime <= 4500000){
            startCountingDown(totalTime.toLong())
        }else{
            startCountingUp(totalTime.toLong())
            binding.timeTitle.text = "Вы находитесь в парковочной зоне:"
        }

        if (qrCode != ""){
            val qrBitmap = qrCodeGenerated(qrCode)
            binding.personalQr.setImageBitmap(qrBitmap)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun qrCodeGenerated(cardCode: String): Bitmap {
        val width = 150
        val height = 150
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

    private fun startCountingDown(timeMillis: Long){
        timer?.cancel()
        timer = object : CountDownTimer(timeMillis, 1000){
            override fun onTick(myTime: Long) {
                val h = (myTime/3600000).toInt()
                val m = ((myTime%3600000)/60000).toInt()
                val s = (((myTime%3600000)%60000)/1000).toInt()
                binding.hours.text = h.toString()
                binding.minutes.text = m.toString()
                binding.seconds.text = s.toString()
            }
            override fun onFinish() {}
        }.start()
    }

    private fun startCountingUp(timeMillis: Long){
        var time = timeMillis
        timer?.cancel()
        timer = object : CountDownTimer((Int.MAX_VALUE).toLong(), 1000){
            override fun onTick(myTime: Long) {
                time+=1000
                val h = (time/3600000).toInt()
                val m = ((time%3600000)/60000).toInt()
                val s = (((time%3600000)%60000)/1000).toInt()
                binding.hours.text = "-$h"
                binding.minutes.text = "-$m"
                binding.seconds.text = "-$s"
            }
            override fun onFinish() {}
        }.start()
    }
}