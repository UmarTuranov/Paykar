package tj.paykar.shop.presentation.shop.basket

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityAddressConfirmBinding
import tj.paykar.shop.presentation.profile.ProfileV2Activity
import tj.paykar.shop.presentation.shop.search.SearchActivity
import java.util.*

class AddressConfirmActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    lateinit var binding: ActivityAddressConfirmBinding
    private var streetIn = ""
    private var houseIn = ""
    private var entranceIn = ""
    private var flatIn = ""
    private var floorIn = ""
    private var commentIn = ""

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var saveDay = 0
    private var saveMonth = 0
    private var saveYear = 0
    private var saveHour = 0
    private var saveMinute = 0

    private var saveDateTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddressConfirmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        getPutIntent()
        setupView()

        pickDate()
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun pickDate() {
        //При нажатии на кнопку вызов фунукции
        binding.apply {
            dateTime.setOnClickListener {
                getDateTimeCalendar()
                DatePickerDialog(this@AddressConfirmActivity, R.style.MyDatePickerStyle, this@AddressConfirmActivity, year, month, day).show()
            }
        }

    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        streetIn = bundle?.getString("street") ?: ""
        houseIn = bundle?.getString("house") ?: ""
    }

    private fun setupView() {
        binding.apply {
            streetText.text = streetIn.toEditable()
            houseText.text = houseIn.toEditable()
            entranceText.text = entranceIn.toEditable()
            flatText.text = flatIn.toEditable()
            floorText.text = floorIn.toEditable()
            commentText.text = commentIn.toEditable()
            val getDate = "$saveDay.$saveMonth.$saveYear"
            val getTime = "$saveHour:$saveMinute"
            resume.setOnClickListener {
                val intent = Intent(this@AddressConfirmActivity, ConfirmOrderActivity::class.java)
                intent.putExtra("deliveryAdress", "${streetText.text.toString()}, ${houseText.text.toString()}, Подъезд ${entranceText.text.toString()}, кв ${flatText.text.toString()}, этаж ${floorText.text.toString()}")
                intent.putExtra("comment", commentText.text.toString())
                intent.putExtra("getDate", getDate)
                intent.putExtra("getTime", getTime)
                startActivity(intent)
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        saveDay = day
        saveMonth = month
        saveYear = year
        getDateTimeCalendar()
        TimePickerDialog(this, R.style.MyDatePickerStyle,this, hour, minute, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        saveHour = hour
        saveMinute = minute

        // Вывод данных на экран
        saveDateTime = "$saveDay.$saveMonth.$saveYear в $saveHour:$saveMinute"
        binding.dateTime.text = "Время доставки: $saveDateTime"
    }

}