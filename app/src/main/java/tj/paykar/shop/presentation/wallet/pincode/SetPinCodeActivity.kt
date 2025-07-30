package tj.paykar.shop.presentation.wallet.pincode

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import tj.paykar.shop.R
import tj.paykar.shop.databinding.WalletActivitySetPinCodeBinding
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.animateTextChange
import tj.paykar.shop.domain.usecase.wallet.functions.backspaceUI
import com.google.android.material.button.MaterialButton
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight

class SetPinCodeActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivitySetPinCodeBinding
    var code: String = ""
    var oldPinCode: String? = ""
    var typedCode: String? = ""
    private var requestType: String? = ""
    var activityType: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivitySetPinCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        val bundle: Bundle? = intent.extras
        requestType = bundle?.getString("requestType", "")
        if (requestType == "set") {
            activityType = "setPinCode"
        } else if (requestType == "reset") {
            activityType = "oldPinCode"
        }
        setupView()
        checkActivityType()
    }

    private fun setupView() = with(binding) {
        oneNumber.click()
        twoNumber.click()
        threeNumber.click()
        fourNumber.click()
        fiveNumber.click()
        sixNumber.click()
        sevenNumber.click()
        eightNumber.click()
        nineNumber.click()
        zeroNumber.click()

        backspaceLayout.setOnClickListener {
            addBlinkEffect(it)
            if (code != ""){
                android.util.Log.d("--P Password", code)
                backspaceUI(code, binding, this@SetPinCodeActivity)
                code = code.removeRange(code.length - 1 until code.length)
            }
        }

        textButton.setOnClickListener {
            addBlinkEffect(it)
            activityType = "setPinCode"
            checkActivityType()
            code = ""
            backspaceUI("", binding, this@SetPinCodeActivity)
            textButton.isGone = true
            errorTitle.isGone = true
            TransitionManager.beginDelayedTransition(binding.root)
        }
    }

    private fun MaterialButton.click() {
        this.setOnClickListener {
            if (code.length < 4) {
                code += this.text
                when(code.length) {
                    1 -> binding.firstNumberTitle.text = this.text
                    2 -> binding.secondNumberTitle.text = this.text
                    3 -> binding.thirdNumberTitle.text = this.text
                    4 -> binding.fourthNumberTitle.text = this.text
                }
                Log.d("--P Password", code)
                Log.d("--P Password", this.text.toString())
                when (activityType) {
                    "oldPinCode" -> {
                        InputUIOldPinCode(code, binding, this@SetPinCodeActivity, this@SetPinCodeActivity).inputUI()
                    }
                    "setPinCode" -> {
                        InputUISetPinCode(code, binding, this@SetPinCodeActivity, this@SetPinCodeActivity).inputUI()
                    }
                    "confirmSetPinCode" -> {
                        InputUIConfirmSetPinCode(code, oldPinCode ?: "", typedCode ?: "", requestType ?: "", binding, this@SetPinCodeActivity).inputUI()
                    }
                }
                addBlinkEffect(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkActivityType() = with(binding) {
        when (activityType) {
            "oldPinCode" -> {
                animateTextChange(infoTitle, "Введите старый PIN-код")
            }
            "setPinCode" -> {
                animateTextChange(infoTitle,"Придумайте новый PIN-код")
            }
            "confirmSetPinCode" -> {
                animateTextChange(infoTitle, "Повторите PIN-код")
                textButton.isVisible = true
            }
        }
    }
}