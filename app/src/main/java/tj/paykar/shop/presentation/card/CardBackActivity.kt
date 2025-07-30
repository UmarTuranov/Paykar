package tj.paykar.shop.presentation.card

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.model.loyalty.InfoCardModel
import tj.paykar.shop.data.storage.NullableHaveStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityCardBackBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.card.authorization.BindCardActivity
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.schedule

@Suppress("DEPRECATION")
class CardBackActivity : AppCompatActivity() {
    lateinit var binding: ActivityCardBackBinding
    private var userInfo: UserCardStorageModel = UserCardStorageModel(0, "false","","","", "", "", "", "", "", "", "", "", "", false, false)
    private var cardInfo: InfoCardModel = InfoCardModel(0, 0, "", "", "", "", "", "", "", 0.0, false, false, false, "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCardBackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        overridePendingTransition(R.anim.scale_in, R.anim.scale_out)
        userInfo = UserStorageData(this@CardBackActivity).getInfoCard()

        if (userInfo.authorized != "") {
            displayBitmap(userInfo.cardCode ?: "")
            updateView()
            val isOnline = MainManagerService().internetConnection(this)
            if (isOnline) {
                updateBalance()
            }
        } else {
            finish()
            startActivity(Intent(this, BindCardActivity::class.java))
        }

        binding.cardViewBack.setOnClickListener {
            try {
                binding.paykarLogo.animate().translationY(0f).duration = 300
                binding.englishHomeLogo.animate().translationY(0f).duration = 300
                finish()
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out)
            }catch (_:Exception){}
        }
        binding.closeBtn.setOnClickListener {
            try {
                binding.paykarLogo.animate().translationY(0f).duration = 300
                binding.englishHomeLogo.animate().translationY(0f).duration = 300
                finish()
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out)
            }catch (_:Exception){}
        }

        Timer().schedule(300) {
            try {
                binding.paykarLogo.animate().translationY(-750f).duration = 300
                binding.englishHomeLogo.animate().translationY(750f).duration = 300
            }catch (_:Exception){}
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        try {
            binding.paykarLogo.animate().translationY(0f).duration = 300
            binding.englishHomeLogo.animate().translationY(0f).duration = 300
            finish()
            overridePendingTransition(R.anim.scale_in, R.anim.scale_out)
        }catch (_:Exception){}
    }

    private fun displayBitmap(value: String) {
        binding.apply {
            imageBarcode.setImageBitmap(
                createBarcodeBitmap(
                    barcodeValue = value,
                    barcodeColor = getColor(R.color.black),
                    backgroundColor = getColor(android.R.color.white),
                    widthPixels = 600,
                    heightPixels = 150
                )
            )
        }
    }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )
        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }
        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }

    @SuppressLint("SetTextI18n")
    private fun updateView() {
        binding.apply {
            cardBalance.text = "${userInfo.balance} баллов"
            cardNumber.text = userInfo.shortCardCode
            val date = MainManagerService().dateConvert(userInfo.lastUpdate ?: "")
            cardUpdate.text = "Обновлено: $date"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBalance() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                cardInfo = CardManagerService().cardInfo(userInfo.phone ?: "", userInfo.cardCode ?: "")
                withContext(Dispatchers.Main) {
                    binding.apply {
                        cardBalance.text = "${cardInfo.Balance} баллов"
                        val dateNow = LocalDateTime.now().toString()
                        val date = MainManagerService().dateConvert(dateNow)
                        cardUpdate.text = "Обновлено: $date"

                        val fToken = UserStorageData(this@CardBackActivity).getFirebaseToken()

                        val card = UserCardStorageModel(
                            cardInfo.ClientId?.toInt() ?: 0,
                            "true",
                            cardInfo.FirstName ?: "",
                            cardInfo.LastName ?: "",
                            cardInfo.SurName ?: "",
                            cardInfo.GenderName,
                            cardInfo.Birthday ?: "",
                            cardInfo.PhoneMobile ?: "",
                            userInfo.token,
                            fToken,
                            "${cardInfo.Balance}",
                            userInfo.cardCode,
                            userInfo.shortCardCode,
                            dateNow,
                            cardInfo.AccumulateOnly ?: false,
                            cardInfo.IsPhoneConfirmed ?: false
                        )

                        if (cardInfo.LastName != null && cardInfo.FirstName != null && cardInfo.Birthday != null) {
                            NullableHaveStorage(this@CardBackActivity).removeNullableHave()
                        }

                        UserStorageData(this@CardBackActivity).saveInfoCard(card)
                    }
                }

            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@CardBackActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@CardBackActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }
}