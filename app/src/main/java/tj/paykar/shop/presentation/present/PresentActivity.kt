package tj.paykar.shop.presentation.present


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bolaware.viewstimerstory.Momentz
import com.bolaware.viewstimerstory.MomentzCallback
import com.bolaware.viewstimerstory.MomentzView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityPresentBinding
import tj.paykar.shop.domain.usecase.PresentManagerService

class PresentActivity : AppCompatActivity(), MomentzCallback {

    lateinit var binding: ActivityPresentBinding

    private var sliders = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service()
    }

    @SuppressLint("ShowToast")
    private fun service() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = PresentManagerService().slideItems()
                if (result.isSuccessful) {
                    sliders = result.body()!!
                    withContext(Dispatchers.Main) {
                        setupView()
                    }
                } else {
                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение!", Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(this@PresentActivity, R.color.red))
                    snack.setTextColor(ContextCompat.getColor(this@PresentActivity, R.color.white))
                    snack.setActionTextColor(ContextCompat.getColor(this@PresentActivity, R.color.white))
                    snack.setAction("Продолжить") {
                        finish()
                        startActivity(Intent(this@PresentActivity, MainActivity::class.java))
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(ContextCompat.getColor(this@PresentActivity, R.color.statusBarBackground))
                snack.setTextColor(ContextCompat.getColor(this@PresentActivity, R.color.white))
                snack.show()
            }
        }
    }

    private fun setupView() {
        val slide1 = ImageView(this@PresentActivity)
        val slide2 = ImageView(this@PresentActivity)
        val slide3 = ImageView(this@PresentActivity)
        val slide4 = ImageView(this@PresentActivity)

        val listOfViews = listOf(
            MomentzView(slide1, 5),
            MomentzView(slide2, 5),
            MomentzView(slide3, 5),
            MomentzView(slide4, 5),
        )

        Momentz(this@PresentActivity, listOfViews,  binding.container,this@PresentActivity).start()
    }

    override fun done() {
        UserStorageData(this).savePresentShow(true)
        finish()
        startActivity(Intent(this@PresentActivity, MainActivity::class.java))
    }

    override fun onNextCalled(view: View, momentz: Momentz, index: Int) {
        if (index == 0) {
            if ((view is ImageView) && (view.drawable == null)) {
                momentz.pause(true)
                view.setImageResource(R.drawable.slide_1)
                view.scaleType = ImageView.ScaleType.CENTER_CROP

                momentz.resume()
            }
        } else if (index == 1) {
            if ((view is ImageView) && (view.drawable == null)) {
                momentz.pause(true)
                view.scaleType = ImageView.ScaleType.CENTER_CROP
                view.setImageResource(R.drawable.slide_2)

                momentz.resume()
            }
        } else if (index == 2) {
            if ((view is ImageView) && (view.drawable == null)) {
                momentz.pause(true)
                view.scaleType = ImageView.ScaleType.CENTER_CROP
                view.setImageResource(R.drawable.slide_3)

                momentz.resume()
            }
        } else if (index == 3) {
            if ((view is ImageView) && (view.drawable == null)) {
                momentz.pause(true)
                view.scaleType = ImageView.ScaleType.CENTER_CROP
                view.setImageResource(R.drawable.slide_4)
                momentz.resume()
            }
        }
    }
}