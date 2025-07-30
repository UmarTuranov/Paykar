package tj.paykar.shop.presentation.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.MyDevicesModel
import tj.paykar.shop.data.model.UserStorageModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivitySettingBinding
import tj.paykar.shop.domain.adapter.my_devices.MyDevicesAdapter
import tj.paykar.shop.domain.usecase.MyDevicesManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.profile.ProfileEditActivity
import java.util.Locale

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding
    private var devicesList = ArrayList<MyDevicesModel>()
    lateinit var userShop: UserStorageModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingBinding.inflate(layoutInflater)
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
        getDevicesList()
        setupView()
    }

    private fun getDevicesList() {
        userShop = UserStorageData(this).getUser()
        val phone = if (userShop.phone?.length == 12){
            userShop.phone?.removeRange(0,3) ?: ""
        } else {
            userShop.phone ?: ""
        }
        val myDevice = getDeviceName()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                devicesList = MyDevicesManagerService().getDevices(phone)
                withContext(Dispatchers.Main){
                    val adapter = MyDevicesAdapter(this@SettingActivity, devicesList, myDevice)
                    binding.apply {
                        myDevicesRv.setHasFixedSize(true)
                        myDevicesRv.layoutManager = LinearLayoutManager(this@SettingActivity, LinearLayoutManager.VERTICAL, false)
                        myDevicesRv.adapter = adapter
                    }
                }
            } catch (_:Exception){}
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val brand = Build.BRAND
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault()).startsWith(manufacturer.lowercase(Locale.getDefault()))) {
            capitalize(model)
        } else {
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

    private fun setupView() {

        binding.apply {

            editAccountBtn.setOnClickListener {
                addBlinkEffect(it)
                startActivity(Intent(this@SettingActivity, ProfileEditActivity::class.java))
            }

            val accessNotification = UserStorageData(this@SettingActivity).getAccessNotification()
            val accessLocation = UserStorageData(this@SettingActivity).getAccessLocation()

            onPush.isChecked = accessNotification
            onLocation.isChecked = accessLocation

            onPush.setOnCheckedChangeListener { buttonView, isChecked ->
                UserStorageData(this@SettingActivity).saveAccessNotification(isChecked)
                val snack = Snackbar.make(root, "Изменения сохранены", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this@SettingActivity, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this@SettingActivity, R.color.white))
                snack.setActionTextColor(ContextCompat.getColor(this@SettingActivity, R.color.white))
                snack.show()
            }

            onLocation.setOnCheckedChangeListener { buttonView, isChecked ->
                UserStorageData(this@SettingActivity).saveAccessLocation(isChecked)
                val snack = Snackbar.make(root, "Изменения сохранены", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this@SettingActivity, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this@SettingActivity, R.color.white))
                snack.setActionTextColor(ContextCompat.getColor(this@SettingActivity, R.color.white))
                snack.show()
            }

            exitButton.setOnClickListener {
                finish()
            }

        }
    }

}