package tj.paykar.shop.presentation.wallet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.AlertGiftBinding
import tj.paykar.shop.databinding.AlertWalletDevelopingBinding
import tj.paykar.shop.databinding.WalletActivityWalletBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.UserManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.InternetConnectionActivity
import tj.paykar.shop.presentation.wallet.login.AuthorizationActivity
import tj.paykar.shop.presentation.wallet.pincode.CodeActivity
import tj.paykar.shop.presentation.wallet.pincode.SetPinCodeActivity
import tj.paykar.shop.presentation.wallet.profile.PersonalDataActivity

class WalletActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityWalletBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        redirect()

    }

    private fun redirect() {
        val isOnline = MainManagerService().internetConnection(this)
        val isRegistration = UserStorage(this).isRegistration
        val walletIsActive = UserStorage(this).walletIsActive
        if (!isOnline) {
            val intent = Intent(this, InternetConnectionActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (walletIsActive == true) {
                if (isRegistration == true) {
                    val intent = Intent(this, SetPinCodeActivity::class.java)
                    intent.putExtra("requestType", "set")
                    startActivity(intent)
                    finish()
                } else if (isRegistration == false) {
                    val pinCodeEntered = UserStorage(this).pinCodeEntered
                    if (pinCodeEntered == true) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, CodeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                setupWalletDeveloping()
            }
        }
    }

    private fun setupWalletDeveloping() {
        binding.root.isVisible = true
        binding.mainActivityBtn.setOnClickListener {
            showViewWithAnimation(it, 400)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}