package tj.paykar.shop.presentation.card.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.CARD
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityBindCardBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.wallet.WalletActivity
import java.time.LocalDateTime

class BindCardActivity : AppCompatActivity() {

    lateinit var binding: ActivityBindCardBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBindCardBinding.inflate(layoutInflater)
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
        window.navigationBarColor = this.resources.getColor(R.color.whiteToBlack)
        setupNaviagation()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun sendSMS(phone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                CardManagerService().sendConfCode(phone)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@BindCardActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@BindCardActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkConfCode(code: String) {
        binding.loading.isVisible = true
        val phone = UserStorageData(this).getCardPhone()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = CardManagerService().checkConfCode(phone, code)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful){
                        val user = response.body()
                        Log.d("--- User", user.toString())
                        val dateNow = LocalDateTime.now().toString()
                        val cardCode = user?.Client!!.Cards[0].CardCode.split("")
                        val shortCardCode = cardCode[7] + cardCode[8] + cardCode[9] + cardCode[10] + cardCode[11] + cardCode[12]
                        val cardInfo = UserCardStorageModel (user.Client.AccountId,"true", user.Client.FullName, "", "",
                            user.Client.Gender,
                            user.Client.Birthday,
                            user.Client.PhoneMobile,
                            user.Token, "",
                            "${user.Client.Balance}",
                            user.Client.Cards[0].CardCode, shortCardCode, dateNow, false, false)

                        UserStorageData(this@BindCardActivity).saveInfoCard(cardInfo)
                        val intent = Intent(this@BindCardActivity, VirtualCardActivity::class.java)
                        startActivity(intent)
                    } else {
                        binding.loading.isVisible = false
                        MaterialAlertDialogBuilder(CARD)
                            .setTitle("Ошибка!")
                            .setMessage("Вы указали неверный код подтверждения. Попробуйте указать еще раз")
                            .setPositiveButton("Попробовать еще") {_,_ -> }
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.loading.isVisible = false
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте обновить страницу позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@BindCardActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@BindCardActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    private fun setupNaviagation() {
        navController = Navigation.findNavController(this, R.id.bind_card_nav)
        CARD = this
    }

    override fun onResume() {
        super.onResume()
        setupButtonMenu()
    }

    private fun setupButtonMenu() {
        val ptoken = PaykarIdStorage(this).userToken
        if (ptoken == "") {
            binding.bottomMenu.setMenuResource(R.menu.bottom_menu_not_authorized)
        } else {
            binding.bottomMenu.setMenuResource(R.menu.button_menu)
        }
        binding.bottomMenu.setItemSelected(R.id.card, true)
        binding.bottomMenu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.catalog -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    startActivity(intent)
                }

                R.id.wallet -> {
                    val intent = Intent(this, WalletActivity::class.java)
                    startActivity(intent)
                }
                R.id.card -> {}
            }
        }
    }
}