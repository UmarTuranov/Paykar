package tj.paykar.shop.presentation.shop.qr_scanner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    lateinit var binding: ActivityResultBinding
    var result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        window.navigationBarColor = this.resources.getColor(R.color.green)
        window.statusBarColor = this.resources.getColor(R.color.green)
        setContentView(binding.root)

        result = intent.extras?.getString("result") ?: ""
        Log.d("--R Result", result)

        //getGift()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
    /*
    private fun getGift(){
        val userInfo = UserStorageData(this).getInfoCard()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val myGift = ReceiveGiftManagerService().receiveGift(
                    userInfo.firstName ?: "",
                    userInfo.lastName ?: "",
                    userInfo.phone ?: "",
                    userInfo.cardCode ?: "",
                    result
                )
                withContext(Dispatchers.Main){
                    if (myGift.body()?.name != null && myGift.body()?.image != null){
                        binding.loading.isVisible = false
                        Log.d("--G Gift", myGift.body().toString())
                        binding.giftName.text = myGift.body()?.name
                        Glide.with(this@ResultActivity)
                            .load("https://mobileapp.paykar.tj/api/gifts/images/" + myGift.body()?.image)
                            .placeholder(R.drawable.nophoto)
                            .into(binding.giftImage)
                    }else{
                        MaterialAlertDialogBuilder(this@ResultActivity)
                            .setTitle("Ошибка проверки данных!")
                            .setMessage("Попробуйте заново")
                            .setPositiveButton("Попробовать заново"){_,_ ->
                                finish()
                            }
                            .show()
                    }
                }
            }catch (e:Exception){
                Log.d("--G Error", e.toString())
                withContext(Dispatchers.Main){
                    MaterialAlertDialogBuilder(this@ResultActivity)
                        .setTitle("Ошибка проверки данных!")
                        .setMessage("Попробуйте заново")
                        .setPositiveButton("Попробовать заново"){_,_ ->
                            finish()
                        }
                        .show()
                }
            }
        }
    }
    */
}