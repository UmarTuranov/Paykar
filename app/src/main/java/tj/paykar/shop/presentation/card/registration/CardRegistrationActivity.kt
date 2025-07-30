package tj.paykar.shop.presentation.card.registration

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.databinding.ActivityCardRegistrationBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.presentation.card.authorization.BindCardActivity
import tj.paykar.shop.presentation.shop.qr_scanner.ScannerActivity

class CardRegistrationActivity : AppCompatActivity() {
    lateinit var binding: ActivityCardRegistrationBinding
    private var isQRCode = false
    private var showLoad = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardRegistrationBinding.inflate(layoutInflater)
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

        handleIntent(intent)
        setupView()
        getScanCardData()
    }

    private fun getScanCardData() {
        val bundle: Bundle? = intent.extras
        isQRCode = bundle?.getBoolean("isQRCode") ?: false
        val cardCode = bundle?.getString("result") ?: ""

        if (cardCode != "") {
            showLoad = true
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(binding.checkCardBtn.windowToken, 0)
            binding.savingProcess.isVisible = true
            binding.checkCardBtn.isVisible = false
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val checkCard = CardManagerService().cardInfo2(cardCode)
                    withContext(Dispatchers.Main) {
                        showLoad = false
                        Log.d("---D CheckCard", checkCard.toString())
                        if (checkCard.isSuccessful) {
                            if (checkCard.body()?.IsPhoneConfirmed == true){
                                binding.savingProcess.isVisible = false
                                binding.checkCardBtn.isVisible = true
                                MaterialAlertDialogBuilder(this@CardRegistrationActivity)
                                    .setTitle("Эта карта уже зарегистрирована!")
                                    .setMessage("Указанная Вами карта уже зарегистрирована, Вы можете привязать вашу карту")
                                    .setPositiveButton("Привязать карту") {_,_ ->
                                        startActivity(Intent(this@CardRegistrationActivity, BindCardActivity::class.java))
                                    }
                                    .setNegativeButton("Отменить") {_,_ ->}
                                    .show()
                            }else{
                                val intent = Intent(this@CardRegistrationActivity, FillCardDataActivity::class.java)
                                intent.putExtra("cardCode", checkCard.body()?.CardCode)
                                intent.putExtra("clientId", checkCard.body()?.ClientId)
                                intent.putExtra("isQRCode", isQRCode)
                                startActivity(intent)
                            }
                        } else {
                            binding.savingProcess.isVisible = false
                            binding.checkCardBtn.isVisible = true
                            MaterialAlertDialogBuilder(this@CardRegistrationActivity)
                                .setTitle("Ошибка!")
                                .setMessage("Вы указали некорректный номер карты")
                                .setPositiveButton("Попробовать еще") {_,_ ->}
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    showLoad = false
                    withContext(Dispatchers.Main) {
                        binding.savingProcess.isVisible = false
                        binding.checkCardBtn.isVisible = true
                        MaterialAlertDialogBuilder(this@CardRegistrationActivity)
                            .setTitle("Сервер недоступен!")
                            .setMessage("Попробуйте позже")
                            .setPositiveButton("Продолжить") {_,_ ->}
                            .show()
                    }
                    Log.d("---D ErrorCard", e.toString())
                }
            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        data?.let {
            val type = it.getQueryParameter("type")
            Log.d("--T Type", type.toString())
            isQRCode = type == "qrcode"
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            if (!showLoad) {
                savingProcess.isVisible = false
                checkCardBtn.isVisible = true
            }
        }
    }

    private fun setupView() {
        binding.apply {

            checkCardBtn.setOnClickListener {
                if (cardNumberText.text?.length == 6){
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(checkCardBtn.windowToken, 0)
                    savingProcess.isVisible = true
                    checkCardBtn.isVisible = false
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val checkCard = CardManagerService().checkCardNumber(cardNumberText.text.toString())
                            withContext(Dispatchers.Main) {
                                Log.d("---D CheckCard", checkCard.toString())
                                if (checkCard.isSuccessful) {
                                    if (checkCard.body()?.IsPhoneConfirmed == true){
                                        savingProcess.isVisible = false
                                        checkCardBtn.isVisible = true
                                        MaterialAlertDialogBuilder(this@CardRegistrationActivity)
                                            .setTitle("Эта карта уже зарегистрирована!")
                                            .setMessage("Указанная Вами карта уже зарегистрирована, Вы можете привязать вашу карту")
                                            .setPositiveButton("Привязать карту") {_,_ ->
                                                startActivity(Intent(this@CardRegistrationActivity, BindCardActivity::class.java))
                                            }
                                            .setNegativeButton("Отменить") {_,_ ->}
                                            .show()
                                    }else{
                                        val intent = Intent(this@CardRegistrationActivity, FillCardDataActivity::class.java)
                                        intent.putExtra("shortCardCode", cardNumberText.text.toString())
                                        intent.putExtra("cardCode", checkCard.body()?.CardCode)
                                        intent.putExtra("clientId", checkCard.body()?.ClientId)
                                        intent.putExtra("isQRCode", isQRCode)
                                        startActivity(intent)
                                    }
                                } else {
                                    savingProcess.isVisible = false
                                    checkCardBtn.isVisible = true
                                    MaterialAlertDialogBuilder(this@CardRegistrationActivity)
                                        .setTitle("Ошибка!")
                                        .setMessage("Вы указали некорректный номер карты")
                                        .setPositiveButton("Попробовать еще") {_,_ ->}
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                savingProcess.isVisible = false
                                checkCardBtn.isVisible = true
                                MaterialAlertDialogBuilder(this@CardRegistrationActivity)
                                    .setTitle("Сервер недоступен!")
                                    .setMessage("Попробуйте позже")
                                    .setPositiveButton("Продолжить") {_,_ ->}
                                    .show()
                            }
                            Log.d("---D ErrorCard", e.toString())
                        }
                    }
                }else{
                    cardLayout.error = "Некорректный номер карты"
                }
            }

            openQRReader.setOnClickListener {
                val intent = Intent(this@CardRegistrationActivity, ScannerActivity::class.java)
                intent.putExtra("isQRCode", isQRCode)
                startActivity(intent)
            }
        }
    }
}