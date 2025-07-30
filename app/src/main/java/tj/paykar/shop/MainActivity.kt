@file:Suppress("DEPRECATION")

package tj.paykar.shop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import tj.paykar.shop.data.PAYKAR_PARKING_SERVICE_ID
import tj.paykar.shop.data.PAY_ORDER_SERVICE_ID
import tj.paykar.shop.data.TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.model.gift.DeferredGiftListModel
import tj.paykar.shop.data.model.gift.PromotionModel
import tj.paykar.shop.data.model.home.HomeModel
import tj.paykar.shop.data.model.home.StoriesModel
import tj.paykar.shop.data.model.id.PaykarIdUserInfoModel
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.data.storage.wallet.SecurityStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.ActivityMainBinding
import tj.paykar.shop.databinding.AlertGiftBinding
import tj.paykar.shop.databinding.BottomSheetDeferredGiftsBinding
import tj.paykar.shop.databinding.BottomSheetGiftBinding
import tj.paykar.shop.databinding.BottomSheetGiftRuleBinding
import tj.paykar.shop.databinding.BottomSheetUpdateApplicationBinding
import tj.paykar.shop.databinding.WalletBottomSheetSavedServicesBinding
import tj.paykar.shop.domain.adapter.gift.DeferredGiftAdapter
import tj.paykar.shop.domain.adapter.home.PromoHomeAdapter
import tj.paykar.shop.domain.adapter.home.VacanciesAdapter
import tj.paykar.shop.domain.adapter.stories.StoriesAdapter
import tj.paykar.shop.domain.adapter.wallet.SavedServicesAdapter
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.domain.usecase.GiftManagerService
import tj.paykar.shop.domain.usecase.HomePageManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.id.PaykarIdManagerService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.IpAddressManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.InternetConnectionActivity
import tj.paykar.shop.presentation.authorization.LoginActivity
import tj.paykar.shop.presentation.card.VirtualCardActivity
import tj.paykar.shop.presentation.notification.NotificationActivity
import tj.paykar.shop.presentation.parking.CarNumberActivity
import tj.paykar.shop.presentation.present.PresentActivity
import tj.paykar.shop.presentation.profile.ProfileV2Activity
import tj.paykar.shop.presentation.profile.reviews.ReviewActivity
import tj.paykar.shop.presentation.shop.ShopActivity
import tj.paykar.shop.presentation.wallet.WalletActivity
import tj.paykar.shop.presentation.wallet.payment.PaymentActivity
import tj.paykar.shop.presentation.wallet.pincode.CodeActivity
import tj.paykar.shop.presentation.wallet.qr.QrScannerActivity
import tj.paykar.shop.presentation.webview.WebViewActivity
import java.time.LocalDateTime

private const val s = "paymentSavedServiceAccount2"

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var giftDialog: Dialog
    private lateinit var giftBinding: AlertGiftBinding
    private lateinit var progressDialog: CustomProgressDialog
    private val promoAdapter = PromoHomeAdapter(this)
    private val storiesAdapter = StoriesAdapter(this)
    private var vacanciesAdapter = VacanciesAdapter(this, arrayListOf())
    private var latitude = ""
    private var longitude = ""

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var userCardInfo: UserCardStorageModel? = null
    private var userShop: UserStorageModel? = null
    private var paykarIdUser: PaykarIdUserInfoModel? = null
    private var shopUserId: Int = 0

    private var fToken = ""
    private var deviceModel = ""
    private var versionOS = ""
    private var typeOS = "Android"
    private var versionApp: String = ""
    private var imei: String = ""
    private var loyaltyToken = ""
    private var ipAddress: String? = null
    private var dateNow: String = ""
    private var walletIsActive: Boolean? = null
    private var isOnline: Boolean = false
    private var presentImage: String? = null

    private var showSavedServicesBottomSheet: Boolean = false

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateAvailable = MutableLiveData<Boolean>().apply { value = false }
    private var updateInfo: AppUpdateInfo? = null
    private var updateListener = InstallStateUpdatedListener { state: InstallState ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("--V VersionName", BuildConfig.VERSION_NAME)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        setupGift()
        setupSplashBackground()
        checkPresentShow()
        initializeVariables()
        hideSystemUI()
        requestReviewFlow(this)

        try {
            appUpdateManager = AppUpdateManagerFactory.create(this)
            appUpdateManager.registerListener(updateListener)
            checkForUpdate()
        } catch (e: Exception) {
            Log.d("--D UpdateError", e.toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { askNotificationPermission() }
        getGeoLocation()
        checkInternet()
    }

    private fun initializeVariables() {
        userShop = UserStorageData(this).getUser()
        shopUserId = UserStorageData(this).getUserId()
        paykarIdUser = PaykarIdStorage(this).getUser()
        userCardInfo = UserStorageData(this).getInfoCard()
        versionOS = DeviceInfo().getVersionOS()
        deviceModel = DeviceInfo().getDeviceModel()
        versionApp = BuildConfig.VERSION_NAME
        imei = DeviceInfo().getDeviceIMEI(this@MainActivity) ?: ""
        fToken = UserStorageData(this).getFirebaseToken()
        dateNow = LocalDateTime.now().toString()
        isOnline = MainManagerService().internetConnection(this)
        progressDialog = CustomProgressDialog(this)
        if (userCardInfo?.authorized == "true") {
            loyaltyToken = userCardInfo?.token ?: ""
        }

        Log.d("--V Variables", "UserShop: $userShop")
        Log.d("--V Variables", "shopUserId: $shopUserId")
        Log.d("--V Variables", "paykarIdUser: $paykarIdUser")
        Log.d("--V Variables", "userCardInfo: $userCardInfo")
        Log.d("--V Variables", "versionOS: $versionOS")
        Log.d("--V Variables", "deviceModel: $deviceModel")
        Log.d("--V Variables", "versionApp: $versionApp")
        Log.d("--V Variables", "imei: $imei")
        Log.d("--V Variables", "fToken: $fToken")
        Log.d("--V Variables", "dateNow: $dateNow")
        Log.d("--V Variables", "loyaltyToken: $loyaltyToken")
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    private fun checkPresentShow() {
        val presentShow = UserStorageData(this@MainActivity).getPresentShow()
        if (!presentShow) {
            startActivity(Intent(this@MainActivity, PresentActivity::class.java))
        }
    }

    private fun setupSplashBackground(){
        val picture = UserStorageData(this@MainActivity).getSplashUrl()
        Glide.with(this@MainActivity)
            .load("https://paykar.shop$picture")
            .into(binding.imageView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (showSavedServicesBottomSheet) {
            showSavedServicesBottomSheet()
        }

        setupWalletBalance()
        setupButtonMenu()
        storiesAdapter.notifyDataSetChanged()
        firebaseToken()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(homeData: Response<HomeModel>) = with(binding) {
        setupPaykarLogo()
        window.navigationBarColor = this@MainActivity.resources.getColor(R.color.whiteToBlack)

        binding.stepView.setSteps(listOf("Заказ в ожидании", "Формируется к отправке", "Заказ в пути", "Заказ доставлен и оплачен"))

        if (homeData.body()?.orderStatus != null) {
            TransitionManager.beginDelayedTransition(binding.root)
            when(homeData.body()?.orderStatus?.orderStatus ?: ""){
                "Заказ в ожидании" -> {
                    binding.orderStatusCV.isVisible = true
                    orderStatusAnimation.setAnimation(R.raw.in_waiting)
                    orderStatusAnimation.playAnimation()
                    orderStatusAnimation.loop(true)
                    orderStatusAnimation.scaleX = 1.0f
                    orderStatusAnimation.scaleY = 1.0f
                }
                "Заказ принят и формируется" -> {
                    binding.orderStatusCV.isVisible = true
                    stepView.go(1, true)
                    orderStatusAnimation.setAnimation(R.raw.forming)
                    orderStatusAnimation.playAnimation()
                    orderStatusAnimation.loop(true)
                    orderStatusAnimation.scaleX = 1.7f
                    orderStatusAnimation.scaleY = 1.7f
                }
                "Заказ доставляется" -> {
                    binding.orderStatusCV.isVisible = true
                    stepView.go(2, true)
                    orderStatusAnimation.setAnimation(R.raw.on_way)
                    orderStatusAnimation.playAnimation()
                    orderStatusAnimation.loop(true)
                    orderStatusAnimation.scaleX = 2.0f
                    orderStatusAnimation.scaleY = 2.0f
                }
            }
        }

        Glide.with(this@MainActivity)
            .load("https://paykar.shop" + homeData.body()?.sliderList?.get(0)?.detail_picture)
            .centerCrop()
            .placeholder(R.drawable.nophoto)
            .into(firstBanner)

        Glide.with(this@MainActivity)
            .load("https://paykar.shop" + homeData.body()?.sliderList?.get(1)?.mobile_picture)
            .centerCrop()
            .placeholder(R.drawable.nophoto)
            .into(secondBanner)

        Glide.with(this@MainActivity)
            .load("https://paykar.shop" + homeData.body()?.sliderList?.get(2)?.mobile_picture)
            .centerCrop()
            .placeholder(R.drawable.nophoto)
            .into(thirdBanner)

        firstBanner.setOnClickListener {
            val intent = Intent(this@MainActivity, WebViewActivity::class.java)
            intent.putExtra("webTitle", homeData.body()?.sliderList?.get(0)?.name)
            intent.putExtra("webUrl", homeData.body()?.sliderList?.get(0)?.link)
            startActivity(intent)
        }

        secondBannerCard.setOnClickListener {
            val intent = Intent(this@MainActivity, WebViewActivity::class.java)
            intent.putExtra("webTitle", homeData.body()?.sliderList?.get(1)?.name)
            intent.putExtra("webUrl", homeData.body()?.sliderList?.get(1)?.link)
            startActivity(intent)
        }

        thirdBannerCard.setOnClickListener {
            val intent = Intent(this@MainActivity, WebViewActivity::class.java)
            intent.putExtra("webTitle", homeData.body()?.sliderList?.get(2)?.name)
            intent.putExtra("webUrl", homeData.body()?.sliderList?.get(2)?.link)
            startActivity(intent)
        }

        val promoList = homeData.body()?.promoList
        promoLinear.isVisible = promoList?.isNotEmpty() == true
        promoAdapter.promoList = promoList ?: arrayListOf()
        promoRecycler.adapter = promoAdapter

        val storiesList = homeData.body()?.storyList
        storiesRecycler.isVisible = storiesList?.isNotEmpty() == true
        storiesAdapter.stories = storiesList?.get(0)?.groupItemList ?: arrayListOf()
        storiesRecycler.adapter = storiesAdapter

        val vacanciesList = homeData.body()?.vacancyList
        vacanciesLinear.isVisible = vacanciesList?.isNotEmpty() == true
        vacanciesAdapter = VacanciesAdapter(this@MainActivity, vacanciesList ?: arrayListOf())
        vacanciesRecycler.adapter = vacanciesAdapter

        loadingProcess.isVisible = false
        showSystemUI()

        if (paykarIdUser?.userToken != "") {
            binding.nameTitle.text = PaykarIdStorage(this@MainActivity).firstName

        } else {
            userDataLinear.isGone = true
            profileBtn.isGone = true
            notificationBtn.isGone = true
            parkingTitle.isGone = true
            parkingLinear.isGone = true
            loginBtn.isVisible = true
        }

        val walletCustomerId = UserStorage(this@MainActivity).customerId

        if (userCardInfo?.authorized == "true") {
            val clientStatus = UserStorageData(this@MainActivity).getClientStatus()
            if (clientStatus.isNotEmpty()) {
                processingStatusTitle.text = clientStatus
                processingStatusCard.isVisible = true
                TransitionManager.beginDelayedTransition(binding.root)
            }
            processingCard.isVisible = true
            binding.apply {
                processingBalanceCard.text = "${userCardInfo!!.balance} б"
            }
        }
        else {
            processingCard.isGone = true
            processingStatusCard.isGone = true
        }

        if (walletCustomerId != 0) {
            setupWalletBalance()
        } else {
            if (processingCard.isGone) {
                cardsLinear.isGone = true
                walletButtonsLayout.isGone = true
            } else {
                walletCard.isGone = true
                walletButtonsLayout.isGone = true
            }
        }

        if (processingCard.isVisible && walletCard.isGone) {
            setMargins(processingCard, null, null, 0, null)
        } else if (processingCard.isGone && walletCard.isVisible) {
            setMargins(walletCard, null, null, null, 0)
        } else {
            setMargins(processingCard, null, null, 8, null)
            setMargins(walletCard, null, null, null, 8)
        }

        if (walletIsActive == false) {
            walletButtonsLayout.isGone = true
        }

        writeFeedback.setOnClickListener {
            if ((paykarIdUser?.userToken ?: "") == "") {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("Авторизация")
                    .setMessage("Чтобы оставить отзыв необходимо пройти авторизацию")
                    .setPositiveButton("Авторизоваться") { _, _ ->
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                    .setNegativeButton("Отмена") { _, _ -> }
                    .show()
            } else {
                startActivity(Intent(this@MainActivity, ReviewActivity::class.java))
            }
        }

        profileBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@MainActivity, ProfileV2Activity::class.java)
            startActivity(intent)
        }

        notificationBtn.setOnClickListener {
            addBlinkEffect(it)
            val intent = Intent(this@MainActivity, NotificationActivity::class.java)
            startActivity(intent)
        }

        walletCard.setOnClickListener {
            val intent = Intent(this@MainActivity, WalletActivity::class.java)
            startActivity(intent)
        }

        processingCard.setOnClickListener {
            val intent = Intent(this@MainActivity, VirtualCardActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }


        savedServicesBtn.setOnClickListener {
            if (UserStorage(this@MainActivity).pinCodeEntered == false) {
                showSavedServicesBottomSheet = true
                val intent = Intent(this@MainActivity, CodeActivity::class.java)
                intent.putExtra("intentTo", "SavedServices")
                startActivity(intent)
            } else {
                showSavedServicesBottomSheet()
            }
        }

        qrBtn.setOnClickListener {
            if (UserStorage(this@MainActivity).pinCodeEntered == false) {
                val intent = Intent(this@MainActivity, CodeActivity::class.java)
                intent.putExtra("intentTo", "QrScannerActivity")
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, QrScannerActivity::class.java)
                startActivity(intent)
            }
        }

        toPaykarWalletBtn.setOnClickListener {
            if (UserStorage(this@MainActivity).pinCodeEntered == false) {
                val intent = Intent(this@MainActivity, CodeActivity::class.java)
                intent.putExtra("intentTo", "PaymentActivity")
                intent.putExtra("serviceId", TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID)
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, PaymentActivity::class.java)
                intent.putExtra("serviceId", TRANSFER_TO_PAYKAR_WALLET_PAYMENT_ID)
                startActivity(intent)
            }
        }

        parkingMyQrButton.setOnClickListener {
            val intent = Intent(this@MainActivity, CarNumberActivity::class.java)
            startActivity(intent)
        }

        parkingPaymentButton.setOnClickListener {
            if (UserStorage(this@MainActivity).pinCodeEntered == false) {
                val intent = Intent(this@MainActivity, CodeActivity::class.java)
                intent.putExtra("intentTo", "PaymentActivity")
                intent.putExtra("serviceId", PAYKAR_PARKING_SERVICE_ID)
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, PaymentActivity::class.java)
                intent.putExtra("serviceId", PAYKAR_PARKING_SERVICE_ID)
                startActivity(intent)
            }
        }

        Log.d("--S Saved", homeData.body()?.orderStatus?.orderId.toString())

        payOrderButton.setOnClickListener {
            if (UserStorage(this@MainActivity).pinCodeEntered == false) {
                val intent = Intent(this@MainActivity, CodeActivity::class.java)
                intent.putExtra("intentTo", "PaymentActivity")
                intent.putExtra("serviceId", PAY_ORDER_SERVICE_ID)
                intent.putExtra("paymentActivityType", "savedService")
                intent.putExtra("paymentSavedServiceAccount", homeData.body()?.orderStatus?.orderId.toString())
                intent.putExtra("paymentSavedServiceAccount2", "")
                intent.putExtra("paymentSavedServicePaymentSum", homeData.body()?.orderStatus?.orderTotalPrice)
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, PaymentActivity::class.java)
                intent.putExtra("serviceId", PAY_ORDER_SERVICE_ID)
                intent.putExtra("activityType", "savedService")
                intent.putExtra("savedServiceAccount", homeData.body()?.orderStatus?.orderId.toString())
                intent.putExtra("savedServiceAccount2", "")
                intent.putExtra("savedServicePaymentSum", homeData.body()?.orderStatus?.orderTotalPrice)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupWalletBalance() = with(binding) {
        val hideWalletBalance = SecurityStorage(this@MainActivity).hideBalanceEnabled
        if (hideWalletBalance == true) {
            walletCardBalance.text = "*****"
        } else {
            val walletAccount = UserStorage(this@MainActivity).getUserInfo()?.Accounts?.find { it.AccountName == "Paykar Wallet" }
            Log.d("--W WalletAccount", walletAccount.toString())
            if (walletAccount != null) {
                val balance = walletAccount.Balance.toString().replace(".", ",")
                walletCardBalance.text = "$balance с"
            }
        }
    }

    private fun setupPaykarLogo() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.paykarLogo.setImageResource(R.drawable.paykar_logo_whitegreen)
            }

            else -> {
                binding.paykarLogo.setImageResource(R.drawable.paykar_logo)
            }
        }
    }

    private fun setupGift() {
        giftDialog = Dialog(this@MainActivity)
        giftBinding = AlertGiftBinding.inflate(LayoutInflater.from(this@MainActivity))
        giftDialog.setContentView(giftBinding.root)
        giftDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowBackground = window.decorView.background
        giftBinding.blurView.setupWith(giftBinding.root, RenderScriptBlur(this@MainActivity))
            .setBlurRadius(10F)
            .setBlurAutoUpdate(true)
            .setFrameClearDrawable(windowBackground)
        giftBinding.blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        giftBinding.blurView.setClipToOutline(true)
    }

    private fun showSavedServicesBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = WalletBottomSheetSavedServicesBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            val servicesList = SavedServicesStorage(this@MainActivity).getSavedServices()
            Log.d("--S ServiceList", servicesList.toString())
            if (servicesList.isEmpty()) {
                recyclerView.isGone = true
                emptyLinear.isVisible = true
            } else {
                val dialog = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                val layoutParams = dialog!!.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.layoutParams = layoutParams
                val behavior = BottomSheetBehavior.from(dialog)
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                behavior.halfExpandedRatio = 0.9F
                recyclerView.isVisible = true
                emptyLinear.isGone = true
                recyclerView.adapter = SavedServicesAdapter(servicesList, this@MainActivity)
            }
        }
    }

    private fun showGiftDialog(promotion: PromotionModel) {
        Glide.with(this@MainActivity)
            .load(presentImage)
            .into(giftBinding.backgroundImage)

        giftDialog.show()
        giftBinding.getGiftButton.setOnClickListener {
            giftDialog.dismiss()
            val bottomSheet = BottomSheetDialog(this@MainActivity)
            val bottomSheetBinding = BottomSheetGiftBinding.inflate(LayoutInflater.from(this@MainActivity))
            bottomSheet.setContentView(bottomSheetBinding.root)
            bottomSheet.show()
            bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBinding.apply {
                Glide.with(this@MainActivity)
                    .load(promotion.gift?.image ?: "")
                    .into(giftImage)

                giftTitle.text = promotion.gift?.title
                giftDescription.text = promotion.gift?.description

                promoRuleTextButton.setOnClickListener {
                    showViewWithAnimation(it, 400)
                    showRuleBottomSheet(promotion.rule ?: "")
                }

                deferButton.setOnClickListener {
                    deferGift(promotion, bottomSheet)
                }

                useButton.setOnClickListener {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Внимание")
                        .setMessage("При нажатии на кнопку Использовать ваш шанс будет аннулирован. Пожалуйста, убедитесь, что хотите продолжить.")
                        .setPositiveButton("Использовать") {_, _ ->
                            useGift(promotion, bottomSheet)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                }
            }
        }
    }

    private fun showRuleBottomSheet(ruleText: String) {
        val ruleBottomSheet = BottomSheetDialog(this@MainActivity)
        val ruleBottomSheetBinding = BottomSheetGiftRuleBinding.inflate(LayoutInflater.from(this@MainActivity))
        ruleBottomSheet.setContentView(ruleBottomSheetBinding.root)
        ruleBottomSheet.show()
        ruleBottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        ruleBottomSheetBinding.apply {
            ruleTitle.text = ruleText

            backButton.setOnClickListener {
                ruleBottomSheet.dismiss()
            }
        }
    }

    private fun showUpdateApplicationBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetUpdateApplicationBinding.inflate(LayoutInflater.from(this))
        bottomSheet.setContentView(bottomSheetBinding.root)
        val dialog = bottomSheet.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = dialog!!.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.layoutParams = layoutParams
        val behavior = BottomSheetBehavior.from(dialog)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
        bottomSheet.setCancelable(false)
        bottomSheet.show()

        bottomSheetBinding.updateButton.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }
    }

    private fun deferGift(promotion: PromotionModel, bottomSheet: BottomSheetDialog) {
        if (!isOnline) {
            tj.paykar.shop.domain.usecase.wallet.MainManagerService().noInternetAlert(this)
        } else {
            progressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = GiftManagerService().deferGift(
                        promotion.PhoneMobile ?: "",
                        promotion.CardCode ?: "",
                        promotion.unit ?: "",
                        shopUserId,
                        promotion.gift?.id ?: 0,
                        "${promotion.FirstName} ${promotion.LastName} ${promotion.SurName}",
                        promotion.gift?.category_gift ?: "",
                        promotion.sum ?: 0.0,
                        promotion.numberCheck ?: ""
                    )
                    withContext(Dispatchers.Main) {
                        if (request.isSuccessful) {
                            val response = request.body()
                            if (response?.status == "success") {
                                bottomSheet.dismiss()
                                if (isOnline) {
                                    checkGiftDeferred()
                                }
                            }
                        }
                    }
                }catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Сервер недоступен, попробуйте по позже!")
                            .setPositiveButton("Понятно") {_, _ ->}
                            .show()
                    }
                }
            }
        }
    }

    private fun useGift(promotion: PromotionModel, bottomSheet: BottomSheetDialog) {
        if (!isOnline) {
            tj.paykar.shop.domain.usecase.wallet.MainManagerService().noInternetAlert(this)
        } else {
            progressDialog.show()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val request = GiftManagerService().receiveGift(
                        promotion.PhoneMobile ?: "",
                        promotion.CardCode ?: "",
                        promotion.unit ?: "",
                        shopUserId,
                        promotion.gift?.id ?: 0,
                        "${promotion.FirstName} ${promotion.LastName} ${promotion.SurName}",
                        promotion.gift?.category_gift ?: "",
                        promotion.sum ?: 0.0,
                        promotion.numberCheck ?: "",
                        "owner"
                    )
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        if (request.isSuccessful) {
                            val response = request.body()
                            if (response?.status == "success") {
                                bottomSheet.dismiss()
                            }
                        }
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setTitle("Произошла ошибка")
                            .setMessage("Сервер недоступен, попробуйте по позже!")
                            .setPositiveButton("Понятно") {_, _ ->

                            }
                    }
                }
            }
        }
    }

    private fun checkInternet() {
        if (!isOnline) {
            startActivity(Intent(this, InternetConnectionActivity::class.java))
        } else {
            service()
        }
    }

    private fun checkGift() {
        if (userCardInfo?.authorized == "true") {
            val cardCode = userCardInfo?.cardCode
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = GiftManagerService().checkGift(cardCode ?: "")
                    withContext(Dispatchers.Main) {
                        if (request.isSuccessful) {
                            val response = request.body()
                            if (response?.response == true) {
                               showGiftDialog(response)
                            } else {
                                giftDialog.dismiss()
                            }
                        }
                    }
                }catch (_: Exception){}
            }
        }
    }

    private fun showDeferredGiftsBottomSheet(giftsList: ArrayList<DeferredGiftListModel>) {
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetDeferredGiftsBinding.inflate(LayoutInflater.from(this))
        bottomSheet.setContentView(bottomSheetBinding.root)
        val dialog = bottomSheet.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = dialog!!.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.layoutParams = layoutParams
        val behavior = BottomSheetBehavior.from(dialog)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.show()
        Log.d("--RequestInfo", giftsList.toString())
        bottomSheetBinding.apply {
            val adapter = DeferredGiftAdapter(giftsList, this@MainActivity)
            giftsListRecycler.adapter = adapter

            backButton.setOnClickListener {
                bottomSheet.dismiss()
            }

            bottomSheet.setOnDismissListener {
                checkGiftDeferred()
            }
        }
    }

    private fun checkGiftDeferred() {
        if (userCardInfo?.authorized == "true") {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = GiftManagerService().deferredGiftList(userCardInfo?.cardCode ?: "")
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        if (request.isSuccessful) {
                            val response = request.body()
                            binding.deferredGiftsButton.isVisible = response?.gifts?.isNotEmpty() == true
                            val windowBackground = window.decorView.background
                            binding.giftBlurView.setupWith(binding.root, RenderScriptBlur(this@MainActivity))
                                .setBlurRadius(10F)
                                .setBlurAutoUpdate(true)
                                .setFrameClearDrawable(windowBackground)
                                .setOverlayColor(ContextCompat.getColor(this@MainActivity, R.color.blurWhiteToBlack))
                            binding.giftBlurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                            binding.giftBlurView.setClipToOutline(true)
                            binding.deferredGiftsButton.setOnClickListener {
                                showDeferredGiftsBottomSheet(response?.gifts ?: arrayListOf())
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun service() {
        val versionApp = BuildConfig.VERSION_NAME
        binding.apply {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    getIpAddress()
                    autoRegistration()
                    val homeData = HomePageManagerService().getHomeData(versionApp, userShop?.id ?: 0)
                    Log.d("--RequestInfo Raw", homeData.raw().toString())
                    withContext(Dispatchers.Main) {
                        Log.d("--H HomeData", homeData.body().toString())
                        if (homeData.body()?.forceUpdate == true) {
                            showUpdateApplicationBottomSheet()
                            return@withContext
                        }

                        val operatingMode = homeData.body()?.operatingMode ?: ""
                        val profileBackgroundUrl = homeData.body()?.splashList?.get(1)?.categoryItemList?.get(0)?.background ?: ""
                        val splashBackground = homeData.body()?.splashList?.get(1)?.categoryItemList?.get(0)?.picture ?: ""
                        val notificationList = homeData.body()?.notificationList?.get(0)?.categoryItemList ?: arrayListOf()
                        walletIsActive = homeData.body()?.walletIsActive

                        UserStorage(this@MainActivity).saveWalletIsActive(walletIsActive ?: false)
                        UserStorageData(this@MainActivity).saveNotificationList(notificationList)
                        UserStorageData(this@MainActivity).saveOperatingModeUrl(operatingMode)

                        if (profileBackgroundUrl != UserStorageData(this@MainActivity).getProfileBgUrl()) {
                            UserStorageData(this@MainActivity).saveProfileBgUrl(profileBackgroundUrl)
                        }

                        if (splashBackground != UserStorageData(this@MainActivity).getSplashUrl()) {
                            UserStorageData(this@MainActivity).saveSplashUrl(splashBackground)
                        }

                        presentImage = homeData.body()?.presentDetail?.image ?: ""
                        setupView(homeData)

                        if (isOnline) {
                            updateUser()
                            checkGift()
                            checkGiftDeferred()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("--E HomeRequest Error", e.toString())
                    withContext(Dispatchers.Main) {
                        val snack = Snackbar.make(
                            binding.root,
                            "Не удаётся установить соединение! Попробуйте обновить страницу позже",
                            Snackbar.LENGTH_LONG
                        )
                        snack.setBackgroundTint(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.statusBarBackground
                            )
                        )
                        snack.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        snack.show()
                    }
                }
            }
        }
    }

    private suspend fun getIpAddress() {
        val ipAddressRequest = IpAddressManagerService().getIpAddress()
        Log.d("--I IpAddress", ipAddressRequest.body().toString())
        val jsonObject: JsonObject = JsonParser.parseString(ipAddressRequest.body().toString()).asJsonObject
        ipAddress = jsonObject.get("ip").asString
        IpAddressStorage(this@MainActivity).saveIpAddress(ipAddress ?: "")
    }

    private suspend fun autoRegistration() {
        if (shopUserId != 0 && paykarIdUser?.userToken == "") {
            val phone = userShop?.phone ?: ""
            val firstName = userShop?.firstName?: ""
            val lastName = userShop?.lastName ?: ""
            val autoRegistration = PaykarIdManagerService().autoRegistration(
                phone,
                firstName,
                lastName,
                deviceModel,
                typeOS,
                versionOS,
                versionApp,
                imei,
                "",
                ipAddress ?: "",
                fToken,
                loyaltyToken
            )
            withContext(Dispatchers.Main) {
                if (autoRegistration.isSuccessful) {
                    val response = autoRegistration.body()
                    Log.d("--RequestInfo", response.toString())
                    if (response?.status == "Successfully") {
                        val shop = response.shop
                        val loyalty = response.loyalty
                        val wallet = response.wallet
                        val userInfo = response.userInfo

                        if (shop != null) {
                            val userStorageModel = UserStorageModel(
                                shop.userId ?: 0,
                                shop.firstName,
                                shop.lastName,
                                shop.phone
                            )
                            UserStorageData(this@MainActivity).saveUser(userStorageModel)
                            userShop = UserStorageData(this@MainActivity).getUser()
                        }

                        if (loyalty != null) {
                            val shortCardCode = loyalty.CardCode?.substring(6, 12)
                            val userCardStorageModel = UserCardStorageModel(
                                loyalty.ClientId ?: 0,
                                "true",
                                loyalty.FirstName ?: "",
                                loyalty.LastName ?: "",
                                loyalty.SurName ?: "",
                                loyalty.GenderName ?: "",
                                loyalty.Birthday ?: "",
                                loyalty.PhoneMobile ?: "",
                                loyaltyToken,
                                fToken,
                                loyalty.Balance.toString(),
                                loyalty.CardCode ?: "",
                                shortCardCode,
                                dateNow,
                                loyalty.AccumulateOnly ?: false,
                                loyalty.IsPhoneConfirmed
                            )
                            UserStorageData(this@MainActivity).saveInfoCard(userCardStorageModel)
                            UserStorageData(this@MainActivity).saveClientStatus(loyalty.ClientStatus ?: "")
                            userCardInfo = UserStorageData(this@MainActivity).getInfoCard()
                        }

                        if (wallet != null) {
                            val customerId = wallet.CustomerId ?: 0
                            val token = wallet.Token ?: ""
                            val isRegistration = wallet.isRegistration ?: false
                            UserStorage(this@MainActivity).saveUser(
                                customerId,
                                phone,
                                token,
                                isRegistration
                            )
                        }

                        if (userInfo != null) {
                            PaykarIdStorage(this@MainActivity).saveUser(userInfo)
                            paykarIdUser = PaykarIdStorage(this@MainActivity).getUser()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUser() {
        if (paykarIdUser?.userToken != "") {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = PaykarIdManagerService().updateUser(
                        paykarIdUser?.userToken ?: "",
                        fToken,
                        loyaltyToken,
                        deviceModel,
                        versionApp,
                        versionOS,
                        imei,
                        "",
                        ipAddress ?: ""
                    )
                    withContext(Dispatchers.Main) {
                        Log.d("--RequestInfo", response.toString())
                        if (response?.status == "successfully") {
                            val loyalty = response.loyalty
                            val wallet = response.wallet
                            val userInfo = response.userInfo

                            if (loyalty != null) {
                                val shortCardCode = loyalty.CardCode?.substring(6, 12)
                                val userCardStorageModel = UserCardStorageModel(
                                    loyalty.ClientId ?: 0,
                                    "true",
                                    loyalty.FirstName ?: "",
                                    loyalty.LastName ?: "",
                                    loyalty.SurName ?: "",
                                    loyalty.GenderName ?: "",
                                    loyalty.Birthday ?: "",
                                    loyalty.PhoneMobile ?: "",
                                    loyaltyToken,
                                    fToken,
                                    loyalty.Balance.toString(),
                                    loyalty.CardCode ?: "",
                                    shortCardCode,
                                    dateNow,
                                    loyalty.AccumulateOnly ?: false,
                                    loyalty.IsPhoneConfirmed
                                )
                                UserStorageData(this@MainActivity).saveInfoCard(userCardStorageModel)
                                UserStorageData(this@MainActivity).saveClientStatus(loyalty.ClientStatus ?: "")
                                userCardInfo = UserStorageData(this@MainActivity).getInfoCard()
                                binding.processingBalanceCard.text = "${userCardInfo?.balance?.replace("null", "0")} б"
                                binding.processingStatusCard.isVisible = true
                                binding.processingStatusTitle.text = loyalty.ClientStatus
                                TransitionManager.beginDelayedTransition(binding.root)
                                showViewWithAnimation(binding.processingBalanceCard, 500)
                            }

                            if (wallet != null) {
                                if(wallet.ResultCode == 0) {
                                    UserStorage(this@MainActivity).saveUserInfo(wallet)
                                    setupWalletBalance()
                                    showViewWithAnimation(binding.walletCardBalance, 500)
                                } else if (wallet.ResultCode == 1) {
                                    UserStorageData(this@MainActivity).cleanStorage()
                                    UserStorage(this@MainActivity).deactivateUser()
                                    PaykarIdStorage(this@MainActivity).deactivateUser()
                                    SecurityStorage(this@MainActivity).clear()
                                    SavedServicesStorage(this@MainActivity).clearSavedServices()
                                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                            if (userInfo != null) {
                                PaykarIdStorage(this@MainActivity).saveUser(userInfo)
                            }

                        }
                        else if (response?.status == "error" && response.message == "Incorrect Token") {
                            UserStorageData(this@MainActivity).cleanStorage()
                            UserStorage(this@MainActivity).deactivateUser()
                            PaykarIdStorage(this@MainActivity).deactivateUser()
                            SecurityStorage(this@MainActivity).clear()
                            SavedServicesStorage(this@MainActivity).clearSavedServices()
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("--Exception", e.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Код ошибки: 700", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupButtonMenu() {
        val ptoken = PaykarIdStorage(this).userToken
        if (ptoken == "") {
            binding.bottomMenu.setMenuResource(R.menu.bottom_menu_not_authorized)
        } else {
            binding.bottomMenu.setMenuResource(R.menu.button_menu)
        }
        binding.bottomMenu.setItemSelected(R.id.home, true)
        binding.bottomMenu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {}
                R.id.catalog -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    startActivity(intent)
                }

                R.id.wallet -> {
                    val intent = Intent(this, WalletActivity::class.java)
                    startActivity(intent)
                }

                R.id.card -> {
                    val intent = Intent(this, VirtualCardActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getGeoLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                latitude = "${it.latitude}"
                longitude = "${it.longitude}"
                Log.d("Your Latitude2", latitude)
                Log.d("Your Longitude2", longitude)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askNotificationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                102
            )
            return
        }
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                updateInfo = it
                updateAvailable.value = true
                Log.d("--D Version Cod Available", it.availableVersionCode().toString())
                startForInAppUpdate(updateInfo)
            } else {
                updateAvailable.value = false
                Log.d("--D Update Not Available", "Not")
            }
        }
    }

    private fun startForInAppUpdate(it: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(it!!, AppUpdateType.IMMEDIATE, this, 1101)
    }

    private fun requestReviewFlow(activity: Activity) {
        val reviewManager = ReviewManagerFactory.create(activity)
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                reviewManager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }

    private fun writeNotify() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                CardManagerService().writeNotify(
                    userCardInfo?.token ?: "",
                    fToken,
                    deviceModel,
                    versionOS
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val snack = Snackbar.make(
                        binding.root,
                        "Не удаётся установить соединение! Попробуйте обновить страницу позже",
                        Snackbar.LENGTH_LONG
                    )
                    snack.setBackgroundTint(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.statusBarBackground
                        )
                    )
                    snack.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    snack.show()
                }
            }
        }
    }

    private fun firebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            fToken = token
            Log.d("---D FToken", token)
            writeNotify()
            val storage = UserStorageData(this)
            storage.saveFirebaseToken(token)
        })
    }

    private fun setMargins(view: View, topMarginDp: Int? = null, bottomMarginDp: Int? = null, startMarginDp: Int? = null, endMarginDp: Int? = null) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        val density = view.resources.displayMetrics.density
        fun convertDpToPx(dp: Int): Int { return (dp * density).toInt() }
        topMarginDp?.let { params.topMargin = convertDpToPx(it) }
        bottomMarginDp?.let { params.bottomMargin = convertDpToPx(it) }
        startMarginDp?.let { params.marginStart = convertDpToPx(it) }
        endMarginDp?.let { params.marginEnd = convertDpToPx(it) }
        view.layoutParams = params
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        try {
            appUpdateManager.unregisterListener(updateListener)
        } catch (e: Exception) {
            Log.d("--D BackError", e.toString())
        }
        moveTaskToBack(true)
    }

}