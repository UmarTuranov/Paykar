package tj.paykar.shop.presentation.card

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.model.loyalty.HistoryCardModel
import tj.paykar.shop.data.model.loyalty.InfoCardModel
import tj.paykar.shop.data.storage.NullableHaveStorage
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityVirtualCardBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.card.authorization.BindCardActivity
import tj.paykar.shop.presentation.card.history.CardHistoryActivity
import tj.paykar.shop.presentation.card.promo.PromoCardActivity
import tj.paykar.shop.presentation.profile.сupons.CuponActivity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.wallet.WalletActivity
import tj.paykar.shop.presentation.webview.WebChatActivity
import tj.paykar.shop.presentation.webview.WebViewActivity
import java.util.*
import kotlin.concurrent.schedule

class VirtualCardActivity : AppCompatActivity() {

    lateinit var binding: ActivityVirtualCardBinding

    val c: Calendar = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    var dayWith0 = ""
    var monthWith0 = ""
    var date = ""

    private var userInfo: UserCardStorageModel = UserCardStorageModel(0, "false","","","", "", "", "", "", "", "", "", "", "", false, false)

    private var cardInfo: InfoCardModel = InfoCardModel(0, 0, "", "", "", "", "", "", "", 0.0, false, false, false, "", "", "")

    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVirtualCardBinding.inflate(layoutInflater)
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
        bottomSheetDialog = BottomSheetDialog(
            this, R.style.BottomSheetDialogTheme
        )
        bottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.bottomsheet_card_binding,
            findViewById(R.id.cardBindingBottomSheet)
        )
        setupBottomSheet()

        getCardInfo()
        if (userInfo.authorized != "") {
            setupView()
            cardClickFunc()
            updateView()
            val isOnline = MainManagerService().internetConnection(this)
            if (isOnline) {
                savePromoCode()
                saveHistory()
                try {
                    Timer().schedule(1000) {
                        try {
                            val intent = Intent(this@VirtualCardActivity, CardBackActivity::class.java)
                            startActivity(intent)
                        } catch (e:Exception){
                            Log.d("--E Exception", e.toString())
                        }
                    }
                    Timer().schedule(800) {
                        try {
                            binding.cardTopShadow.animate().translationX(0f).translationY(0f).rotation(0f).duration = 200
                            binding.cardBottomShadow.animate().translationX(0f).translationY(0f).rotation(0f).duration = 200
                        }catch (_:Exception){}
                    }
                }catch (_:Exception){}
            }
        } else {
            finish()
            startActivity(Intent(this, BindCardActivity::class.java))
        }

        Log.d("---D  CardInfo", cardInfo.toString())
        Log.d("---D  UserInfo", userInfo.toString())
    }

    override fun onRestart() {
        super.onRestart()
        val nullableHave = NullableHaveStorage(this).getHaveNullable()
        if (nullableHave == ""){
            bottomSheetDialog.dismiss()
        }
        binding.apply {
            try {
                Timer().schedule(250){
                    try {
                        cardTopShadow.animate().translationX(-30f).translationY(-44f).rotation(12f).duration = 200
                        cardBottomShadow.animate().translationX(-30f).translationY(44f).rotation(-12f).duration = 200
                    }catch (_:Exception){}
                }
            }catch (_:Exception){}
        }
    }

    private fun cardClickFunc(){
        binding.apply {
            cardFront.setOnClickListener {
                try {
                    Timer().schedule(150) {
                        try {
                            val intent = Intent(this@VirtualCardActivity, CardBackActivity::class.java)
                            startActivity(intent)
                        }catch (_:Exception){}
                    }
                    cardTopShadow.animate().translationX(0f).translationY(0f).rotation(0f).duration = 200
                    cardBottomShadow.animate().translationX(0f).translationY(0f).rotation(0f).duration = 200
                }catch (_:Exception){}
            }
        }
    }

    private fun setupBottomSheet() {
        val phone = UserStorageData(this).getCardPhone()
        Log.d("VirtualCard Phone", phone)
        getCardInfo()
        val nullableHave = NullableHaveStorage(this).getHaveNullable()
        //---------------------------------- BottomSheet -----------------------------------

        val birthdayLayout = bottomSheetView.findViewById<TextInputLayout>(R.id.birthdayLayout)
        val birthday = bottomSheetView.findViewById<TextInputEditText>(R.id.birthday)
        val sendMyData = bottomSheetView.findViewById<MaterialButton>(R.id.sendMyData)
        val firstNameLayout = bottomSheetView.findViewById<TextInputLayout>(R.id.firstNameLayout)
        val lastNameLayout = bottomSheetView.findViewById<TextInputLayout>(R.id.lastNameLayout)
        val firstName = bottomSheetView.findViewById<TextInputEditText>(R.id.firstName)
        val lastName = bottomSheetView.findViewById<TextInputEditText>(R.id.lastName)

        birthdayLayout.setEndIconOnClickListener {
            DatePickerDialog(this, { _, myear, mmonth, mday ->
                dayWith0 = if (mday<10){
                    "0$mday"
                }else{
                    "$mday"
                }
                monthWith0 = if (mmonth<9){
                    "0${mmonth + 1}"
                }else{
                    "${mmonth + 1}"
                }
                date = "$myear-$monthWith0-$dayWith0"
                birthday.text = "$dayWith0.$monthWith0.$myear".toEditable()
            }, year, month, day).show()
        }

        sendMyData.setOnClickListener {
            val isOnline = MainManagerService().internetConnection(this)
            if (!isOnline) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Нет подключения к интернету!")
                    .setMessage("Подключение к интернету отсутсвует, проверьте соединение с интернетом и повторите попытку")
                    .setPositiveButton("Продолжить") {_,_ ->}
                    .show()
            }else{
                if (firstName.text.toString() != "" && lastName.text.toString() != "" && birthday.text.toString() != ""){
                    bottomSheetDialog.dismiss()
                    binding.loading.isVisible = true
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val cardInfo = CardManagerService().cardInfo("", userInfo.cardCode ?: "")
                            val cardUpdate = CardManagerService().saveCardInfo( cardInfo.ClientId ?: 0, lastName.text.toString(), firstName.text.toString(),
                                "",
                                date,
                                phone,
                                "",
                                "",
                                "",
                                "", 0, false)
                            Log.d("--- Your CardUpdate Process", cardUpdate.body().toString())

                            if (cardUpdate.isSuccessful){
                                try {
                                    userInfo.firstName = firstName.text.toString()
                                    userInfo.lastName = lastName.text.toString()
                                    userInfo.birthday = date
                                    UserStorageData(this@VirtualCardActivity).saveInfoCard(userInfo)
                                    NullableHaveStorage(this@VirtualCardActivity).removeNullableHave()
                                    withContext(Dispatchers.Main){
                                        startActivity(Intent(this@VirtualCardActivity, VirtualCardActivity::class.java))
                                    }
                                }catch (e: Exception){
                                    Log.d("--- Another Error", e.toString())
                                    withContext(Dispatchers.Main){
                                        binding.loading.isVisible = false
                                        val alert = MaterialAlertDialogBuilder(this@VirtualCardActivity)
                                            .setTitle("Сервер недоступен!")
                                            .setMessage("Попробуйте позже")
                                            .setPositiveButton("Продолжить") {_,_ -> finish()}
                                            .show()

                                        alert.setOnDismissListener {
                                            finish()
                                        }
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main){
                                    binding.loading.isVisible = false
                                    val alert = MaterialAlertDialogBuilder(this@VirtualCardActivity)
                                        .setTitle("Сервер недоступен!")
                                        .setMessage("Попробуйте позже")
                                        .setPositiveButton("Продолжить") {_,_ -> finish()}
                                        .show()

                                    alert.setOnDismissListener {
                                        finish()
                                    }
                                }
                            }
                        }catch (e: Exception){
                            Log.d("--- Error", e.toString())
                            withContext(Dispatchers.Main){
                                binding.loading.isVisible = false
                                val alert = MaterialAlertDialogBuilder(this@VirtualCardActivity)
                                    .setTitle("Сервер недоступен!")
                                    .setMessage("Попробуйте позже")
                                    .setPositiveButton("Продолжить") {_,_ -> finish()}
                                    .show()

                                alert.setOnDismissListener {
                                    finish()
                                }
                            }
                        }
                    }
                }else if (firstName.text.toString() == ""){
                    firstNameLayout.isErrorEnabled = true
                    firstNameLayout.error = "Укажите ваше Имя"
                }else if (lastName.text.toString() == ""){
                    lastNameLayout.isErrorEnabled = true
                    lastNameLayout.error = "Укажите вашу Фамилию"
                }else if (birthday.text.toString() == ""){
                    birthdayLayout.isErrorEnabled = true
                    birthdayLayout.error = "Укажите дату рождения"
                }
            }
        }

        if (nullableHave == "yes"){
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        //--------------------------------------------------------------------------------
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        binding.apply {
            historyCardBtn.setOnClickListener {
                val intent = Intent(this@VirtualCardActivity, CardHistoryActivity::class.java)
                startActivity(intent)
            }
            myCuponsBtn.setOnClickListener {
                val intent = Intent(this@VirtualCardActivity, CuponActivity::class.java)
                startActivity(intent)
            }
            promoCardBtn.setOnClickListener {
                val intent = Intent(this@VirtualCardActivity, PromoCardActivity::class.java)
                startActivity(intent)
            }
            roolCardBtn.setOnClickListener {
                val intent = Intent(this@VirtualCardActivity, WebViewActivity::class.java)
                intent.putExtra("webTitle", "Основные правила")
                intent.putExtra("webUrl", "https://paykar.tj/company/loyality.php")
                startActivity(intent)
            }
            webChatBtn.setOnClickListener {
                val intent = Intent(this@VirtualCardActivity, WebChatActivity::class.java)
                startActivity(intent)
            }

            val clientStatus = UserStorageData(this@VirtualCardActivity).getClientStatus()
            if (clientStatus.isNotEmpty()) {
                processingStatusTitle.text = clientStatus
            }
//            untieCardBtn.setOnClickListener {
//                //Удалить данные с памяти
//                MaterialAlertDialogBuilder(this@VirtualCardActivity)
//                    .setTitle(resources.getString(R.string.alertCardTitle))
//                    .setMessage(resources.getString(R.string.alertProfileDescription))
//                    .setNegativeButton(resources.getString(R.string.alertProfileCancelBtn)) { dialog, which ->
//                        // Respond to negative button press
//                    }
//                    .setPositiveButton(resources.getString(R.string.alertCardBtn)) { dialog, which ->
//                        UserStorageData(this@VirtualCardActivity).cleanInfoCard()
//                        finish()
//                        startActivity(Intent(this@VirtualCardActivity, MainActivity::class.java))
//                    }.show()
//            }
        }
    }

    private fun getCardInfo() {
        userInfo = UserStorageData(this@VirtualCardActivity).getInfoCard()
    }

    private fun saveHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sendReq = CardManagerService().historyCardList(userInfo.token ?: "")
                val response: ArrayList<HistoryCardModel> = sendReq
                UserStorageData(this@VirtualCardActivity).saveCardHistory(response)
            }catch (e: Exception){
                Log.d("--Exception", e.toString())
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@VirtualCardActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@VirtualCardActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    private fun savePromoCode() {
        val token = userInfo.token ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val promoList = CardManagerService().promoCode(token)
                Log.d("---D Promo", promoList.body().toString())
                if (promoList.isSuccessful) {
                    val user = promoList.body()
                    val cards = user?.Client?.Cards
                    if (cards != null) {
                        UserStorageData(this@VirtualCardActivity).saveCardList(cards)
                        val contentList = CardManagerService().promoContent(token)
                        Log.d("---D Content", contentList.body().toString())
                        if (contentList.isSuccessful) {
                            val content = contentList.body()
                            if (content != null) {
                                UserStorageData(this@VirtualCardActivity).savePromoList(content)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("--Exception", e.toString())
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@VirtualCardActivity, R.color.statusBarBackground))
                    snack.setTextColor(ContextCompat.getColor(this@VirtualCardActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateView() {
        binding.apply {
            cardTitle.text = "${userInfo.firstName} ${userInfo.lastName}"
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

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