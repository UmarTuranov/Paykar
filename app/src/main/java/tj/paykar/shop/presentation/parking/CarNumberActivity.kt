package tj.paykar.shop.presentation.parking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.CarNumberStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityCarNumberBinding
import tj.paykar.shop.domain.usecase.CarParkManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import java.util.Timer
import kotlin.concurrent.schedule

class CarNumberActivity : AppCompatActivity() {
    lateinit var binding: ActivityCarNumberBinding
    private lateinit var progressDialog: CustomProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
        window.statusBarColor = this.resources.getColor(R.color.greyActivityBackground)
        window.navigationBarColor = this.resources.getColor(R.color.greyActivityBackground)
        binding.lastVisitCV.isVisible = false
        setupView()
    }

    override fun onResume() {
        super.onResume()
        progressDialog = CustomProgressDialog(this)
        progressDialog.dismiss()
    }

    private fun setupView() {
        binding.apply {
            val carsNumbersList = CarNumberStorage(this@CarNumberActivity).getCarNumberList()
            Log.d("--L CarsNumber", carsNumbersList.toString())
            if (carsNumbersList.isNotEmpty()){
                plateNumber.text = carsNumbersList.last().toEditable()
                val carsNumbersAdapter = ArrayAdapter(this@CarNumberActivity, R.layout.list_item, carsNumbersList)
                (plateNumberLayout.editText as? AutoCompleteTextView)?.setAdapter(carsNumbersAdapter)
                getLastVisit(carsNumbersList.last())

                (plateNumberLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener {_,_,position,_ ->
                    getLastVisit(carsNumbersList[position])
                }
            }

            iAccept.isChecked = true

            val tvTerms = binding.iAccept
            val text = this@CarNumberActivity.resources.getString(R.string.i_accept)
            val spannableString = SpannableString(text)
            val offerStart = text.indexOf("Пользовательского соглашения")
            val offerEnd = offerStart + "Пользовательского соглашения".length
            val offerSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://paykar.tj/include/licenses_detail.php"))
                    startActivity(intent)
                }
            }

            val green = this@CarNumberActivity.resources.getColor(R.color.green)
            spannableString.setSpan(offerSpan, offerStart, offerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(green), offerStart, offerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tvTerms.text = spannableString
            tvTerms.movementMethod = LinkMovementMethod.getInstance()

            getQrCodeBtn.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(getQrCodeBtn.windowToken, 0)
                val isOnline = MainManagerService().internetConnection(this@CarNumberActivity)
                if (plateNumber.text.toString() != "" && iAccept.isChecked){
                    if (!isOnline){
                        MaterialAlertDialogBuilder(this@CarNumberActivity)
                            .setTitle("Нет интернет соединения!")
                            .setMessage("Подключитесь к интернету и повторите попытку")
                            .setPositiveButton("Понятно"){_,_ ->}
                            .show()
                    }else{
                        progressDialog.show()
                        val pNum = plateNumber.text.toString().uppercase()
                        if (rememberMySN.isChecked){
                            CarNumberStorage(this@CarNumberActivity).saveCarNumber(pNum)
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val carParkingInfo = CarParkManagerService().checkCarNumber(pNum)
                                withContext(Dispatchers.Main){
                                    if (carParkingInfo.isSuccessful){
                                        Log.d("---D CarParkingRequest", carParkingInfo.toString())
                                        Log.d("---D CarParkingRequestBody", carParkingInfo.body().toString())

                                        if (carParkingInfo.body()?.status == "entry"){
                                            Timer().schedule(1500) {
                                                val intent = Intent(this@CarNumberActivity, PersonalQrActivity::class.java)
                                                intent.putExtra("qrCode", carParkingInfo.body()?.qrCode)
                                                intent.putExtra("limitDate", carParkingInfo.body()?.limitDate)
                                                startActivity(intent)
                                            }
                                        }else{
                                            progressDialog.dismiss()
                                            MaterialAlertDialogBuilder(this@CarNumberActivity)
                                                .setTitle("Предупреждение!")
                                                .setMessage("Ваш визит в парковочную зону не зафиксирован")
                                                .setPositiveButton("Понятно"){_,_ ->}
                                                .show()
                                        }
                                    }else{
                                        progressDialog.dismiss()
                                        MaterialAlertDialogBuilder(this@CarNumberActivity)
                                            .setTitle("Сервер недоступен!")
                                            .setMessage("Попробуйте позже")
                                            .setPositiveButton("Понятно"){_,_ ->}
                                            .show()
                                    }
                                }
                            }catch (e:Exception){
                                withContext(Dispatchers.Main){
                                    progressDialog.dismiss()
                                    MaterialAlertDialogBuilder(this@CarNumberActivity)
                                        .setTitle("Сервер недоступен!")
                                        .setMessage("Попробуйте позже")
                                        .setPositiveButton("Понятно"){_,_ ->}
                                        .show()
                                }
                            }
                        }
                    }
                }
                else if (plateNumber.text.toString() == ""){
                    MaterialAlertDialogBuilder(this@CarNumberActivity)
                        .setTitle("Введите номер автомобиля!")
                        .setPositiveButton("Понятно"){_,_ ->}
                        .show()
                }
                else if (!iAccept.isChecked){
                    MaterialAlertDialogBuilder(this@CarNumberActivity)
                        .setMessage("Пожалуйста подтвердите ваше согласие с условиями Пользовательского соглашения!")
                        .setPositiveButton("Понятно"){_,_ ->}
                        .show()
                }
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun getLastVisit(plateNumber: String){
        val userId = UserStorageData(this@CarNumberActivity).getUserId()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lastVisit = CarParkManagerService().getLastVisit(userId, plateNumber)
                Log.d("--L LastVisit", lastVisit.body().toString())
                withContext(Dispatchers.Main){
                    if (lastVisit.body()?.id != 0){
                        binding.apply {
                            val lastV = lastVisit.body()
                            lVStatus.text = lastV?.status
                            lVEntryTime.text = lastV?.entryTime
                            lVExitTime.text = lastV?.exitTime
                            lVTotalTime.text = lastV?.totalTime
                            lVTariffName.text = lastV?.tariffName
                            lVPaid.text = lastV?.paid
                            lastVisitCV.isVisible = true
                            TransitionManager.beginDelayedTransition(binding.root)
                        }
                    }else{
                        binding.lastVisitCV.isVisible = false
                        TransitionManager.beginDelayedTransition(binding.root)
                    }
                }
            }catch (e:Exception){
                Log.d("--E Error", e.toString())
            }
        }
    }
}