package tj.paykar.shop.domain.usecase.wallet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import tj.paykar.shop.R
import tj.paykar.shop.databinding.WalletCustomProgressDialogBinding

@SuppressLint("InflateParams", "MissingInflatedId")
class CustomProgressDialog(context: Context): Dialog(context) {
    val binding = WalletCustomProgressDialogBinding.inflate(layoutInflater)
    init {
        binding.lottieAnimationView.setAnimation(R.raw.loading_animation)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.clProgress)
        setCancelable(false)
    }
}