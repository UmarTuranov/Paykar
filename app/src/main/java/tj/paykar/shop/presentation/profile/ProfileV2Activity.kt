package tj.paykar.shop.presentation.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.AddressModel
import tj.paykar.shop.data.model.shop.PreferencesModel
import tj.paykar.shop.data.model.UserCardStorageModel
import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.storage.AddressStorage
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.data.storage.wallet.SecurityStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.ActivityProfileV2Binding
import tj.paykar.shop.databinding.BottomSheetAddressBinding
import tj.paykar.shop.databinding.BottomSheetAddressInfoBinding
import tj.paykar.shop.domain.adapter.address.AddressAdapter
import tj.paykar.shop.domain.adapter.preferences.PreferencesAdapter
import tj.paykar.shop.domain.usecase.LogOutManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.shop.OrderStatusManagerService
import tj.paykar.shop.domain.usecase.shop.PreferencesManagerService
import tj.paykar.shop.presentation.InternetConnectionActivity
import tj.paykar.shop.presentation.shop.basket.MyBasketsActivity
import tj.paykar.shop.presentation.notification.NotificationActivity
import tj.paykar.shop.presentation.parking.CarNumberActivity
import tj.paykar.shop.presentation.profile.history.OrderHistoryActivity
import tj.paykar.shop.presentation.shop.promo.PromoActivity
import tj.paykar.shop.presentation.setting.SettingActivity
import tj.paykar.shop.presentation.webview.WebChatActivity
import tj.paykar.shop.presentation.shop.wishlist.WishlistActivity
import java.util.Locale

class ProfileV2Activity : AppCompatActivity(),
    tj.paykar.shop.domain.reprository.shop.PreferencesItemClickManager,
    tj.paykar.shop.domain.reprository.shop.AddressSelectedManager {

    lateinit var binding: ActivityProfileV2Binding
    lateinit var bottomSheet: BottomSheetDialog
    lateinit var bottomSheetView: View

    private var userInfo: UserCardStorageModel = UserCardStorageModel(0, "false","","","", "", "", "", "", "", "", "", "", "", false, false)
    private var userShop: UserStorageModel = UserStorageModel(0, "", "", "",)

    private var savePreferencesButton: MaterialButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileV2Binding.inflate(layoutInflater)
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
        getUserShop()
        getCardInfo()
        setupView()
        checkInternet()
        preferencesSetup()
    }

    private fun checkInternet() {
        val isOnline = MainManagerService().internetConnection(this)
        if (!isOnline) {
            startActivity(Intent(this, InternetConnectionActivity::class.java))
        }
    }

    private fun getUserShop() {
        val user = UserStorageData(this).getUser()
        userShop = user
    }

    private fun getCardInfo() {
        userInfo = UserStorageData(this).getInfoCard()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        binding.apply {
            val paykarIdUserInfo = PaykarIdStorage(this@ProfileV2Activity).getUser()
            userName.text = paykarIdUserInfo.firstName + " " + paykarIdUserInfo.lastName
            userPhone.text = "+992" + paykarIdUserInfo.phone

            myAddressesBtn.setOnClickListener{
                showAddressBottomSheet()
            }
            myBasketsBtn.setOnClickListener{
                val intent = Intent(this@ProfileV2Activity, MyBasketsActivity::class.java)
                startActivity(intent)
            }
            notifyButton.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, NotificationActivity::class.java)
                startActivity(intent)
            }
            settingButton.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, SettingActivity::class.java)
                startActivity(intent)
            }

            proWishlistBtn.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, WishlistActivity::class.java)
                startActivity(intent)
            }
            proOrderHistoyBtn.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, OrderHistoryActivity::class.java)
                startActivity(intent)
            }
            proSalesBtn.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, PromoActivity::class.java)
                startActivity(intent)
            }
            proChatBtn.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, WebChatActivity::class.java)
                startActivity(intent)
            }
            parkingBtn.setOnClickListener {
                val intent = Intent(this@ProfileV2Activity, CarNumberActivity::class.java)
                startActivity(intent)
            }
            myPreferencesBtn.setOnClickListener {
                bottomSheet.show()
            }

            proExitBtn.setOnClickListener {
                MaterialAlertDialogBuilder(this@ProfileV2Activity)
                    .setTitle(resources.getString(R.string.alertProfileTitle))
                    .setMessage(resources.getString(R.string.alertProfileDescription))
                    .setNegativeButton(resources.getString(R.string.alertProfileCancelBtn)) {_,_ ->}
                    .setPositiveButton(resources.getString(R.string.alertProfileConfBtn)) {_,_ ->
                        logOut()
                        UserStorageData(this@ProfileV2Activity).cleanStorage()
                        UserStorage(this@ProfileV2Activity).deactivateUser()
                        PaykarIdStorage(this@ProfileV2Activity).deactivateUser()
                        SecurityStorage(this@ProfileV2Activity).clear()
                        SavedServicesStorage(this@ProfileV2Activity).clearSavedServices()
                        finish()
                        startActivity(Intent(this@ProfileV2Activity, MainActivity::class.java))
                    }
                    .show()
            }
        }
    }

    private fun logOut(){
        val deviceModel = getDeviceName()
        val phone = if (userShop.phone?.length == 12){
            userShop.phone?.removeRange(0,3) ?: ""
        } else {
            userShop.phone ?: ""
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                LogOutManagerService().logOut(phone, deviceModel)
            }catch (e:Exception){ Log.d("--E Exception", e.toString()) }
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val brand = Build.BRAND
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault()).startsWith(manufacturer.lowercase(Locale.getDefault()))) {
            capitalize(model)
        }
        else if(brand.lowercase(Locale.getDefault()).startsWith(manufacturer.lowercase(Locale.getDefault()))){
            "$brand $model"
        }
        else {
            capitalize(manufacturer) + " " + brand + " " + model
        }
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    private fun showAddressBottomSheet() {
        val addressList = ArrayList<AddressModel>()
        addressList.addAll(AddressStorage(this).getAddressList() ?: arrayListOf())
        val adapter = AddressAdapter(this@ProfileV2Activity, addressList, "createNewAddress", this)
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetAddressBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            addNewAddress.setOnClickListener {
                showAddressInfoBottomSheet(adapter)
            }
            addressRecycler.setHasFixedSize(true)
            addressRecycler.layoutManager = LinearLayoutManager(this@ProfileV2Activity, LinearLayoutManager.VERTICAL, false)
            addressRecycler.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAddressInfoBottomSheet(adapter: AddressAdapter) {
        val addressList = ArrayList<AddressModel>()
        addressList.addAll(AddressStorage(this).getAddressList() ?: arrayListOf())
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetAddressInfoBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            saveBtn.setOnClickListener {
                if (streetText.text.isNullOrEmpty()) {
                    street.error = "Обязательное поле"
                } else {
                    val myAddress = AddressModel(streetText.text.toString(), houseText.text.toString(), entranceText.text.toString(), flatText.text.toString(), floorText.text.toString(), true)
                    AddressStorage(this@ProfileV2Activity).saveAddress(myAddress)
                    adapter.updateList()
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

    private fun preferencesSetup() {
        bottomSheet = BottomSheetDialog(
            this, R.style.BottomSheetDialogTheme
        )
        bottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_preferences,
            findViewById(R.id.preferencesBSh)
        )
        bottomSheet.setContentView(bottomSheetView)
        bottomSheet.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

        bottomSheet.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val dialog = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val layoutParams = dialog!!.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.layoutParams = layoutParams
            val behavior = BottomSheetBehavior.from(dialog)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val preferencesAdapter = PreferencesAdapter(this, this)
        savePreferencesButton = bottomSheetView.findViewById(R.id.saveBtn)
        val preferencesRV: RecyclerView = bottomSheetView.findViewById(R.id.preferencesRV)
        val loadingAnimation: CardView = bottomSheetView.findViewById(R.id.loading)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferencesList = PreferencesManagerService().getPreferencesList()
                val userPreferencesList = PreferencesManagerService().getUserPreferences(userShop.id)
                for (p in userPreferencesList) {
                    preferencesList.find { it.id == p.id }?.selected = true
                }
                withContext(Dispatchers.Main){
                    preferencesRV.setHasFixedSize(true)
                    val layoutManager = FlexboxLayoutManager(this@ProfileV2Activity)
                    layoutManager.flexDirection = FlexDirection.ROW
                    layoutManager.justifyContent = JustifyContent.FLEX_START
                    preferencesRV.layoutManager = layoutManager
                    preferencesAdapter.preferencesList = preferencesList
                    preferencesRV.adapter = preferencesAdapter
                }
            }catch (e: Exception){
                Log.d("--E Error", e.toString())
                if (!isFinishing){
                    withContext(Dispatchers.Main){
                        MaterialAlertDialogBuilder(this@ProfileV2Activity)
                            .setTitle("Сервер недоступен!")
                            .setMessage("Попробуйте позже")
                            .show()
                    }
                }
            }
        }

        savePreferencesButton?.setOnClickListener {
            val isOnline = MainManagerService().internetConnection(this@ProfileV2Activity)
            if (isOnline) {
                loadingAnimation.isVisible = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        PreferencesManagerService().savePreferences(userShop.id, preferencesAdapter.preferencesList)
                        withContext(Dispatchers.Main){
                            bottomSheet.dismiss()
                        }
                    }
                    catch (e: Exception){
                        Log.d("--E Error", e.toString())
                        withContext(Dispatchers.Main){
                            MaterialAlertDialogBuilder(this@ProfileV2Activity)
                                .setTitle("Сервер недоступен!")
                                .setMessage("Попробуйте позже")
                                .show()
                        }
                    }
                }
            }
            else {
                MaterialAlertDialogBuilder(this@ProfileV2Activity)
                    .setTitle("Нет интернета!")
                    .setMessage("Включите интернет и повторите попытку")
                    .show()
            }
        }
    }

    override fun checkSelected(preferences: ArrayList<PreferencesModel>) {
        savePreferencesButton?.isEnabled = false
        for (p in preferences){
            if (p.selected == true){
                savePreferencesButton?.isEnabled = true
                break
            }
        }
    }

    override fun onSelected(address: AddressModel) {}
}