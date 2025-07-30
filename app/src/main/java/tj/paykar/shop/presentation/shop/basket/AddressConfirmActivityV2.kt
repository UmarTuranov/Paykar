package tj.paykar.shop.presentation.shop.basket

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.AddressModel
import tj.paykar.shop.data.storage.AddressStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityAddressConfirmV2Binding
import tj.paykar.shop.databinding.BottomSheetAddressBinding
import tj.paykar.shop.databinding.BottomSheetAddressInfoBinding
import tj.paykar.shop.domain.adapter.address.AddressAdapter
import tj.paykar.shop.domain.adapter.basket.BasketProductAdapter
import tj.paykar.shop.domain.reprository.shop.AddressSelectedManager
import java.util.*

class AddressConfirmActivityV2 : AppCompatActivity(),
    tj.paykar.shop.domain.reprository.shop.AddressSelectedManager {
    lateinit var binding: ActivityAddressConfirmV2Binding
    var adapter = BasketProductAdapter(this@AddressConfirmActivityV2)
    var address: AddressModel? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressConfirmV2Binding.inflate(layoutInflater)
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
        bottomSheetDialog = BottomSheetDialog(this)
    }

    override fun onResume() {
        super.onResume()
        setupView()
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.apply {
            adapter.basketProductList = UserStorageData(this@AddressConfirmActivityV2).getBasketProductList() ?: arrayListOf()
            productRecycle.setHasFixedSize(true)
            productRecycle.layoutManager = LinearLayoutManager(this@AddressConfirmActivityV2, LinearLayoutManager.HORIZONTAL, false)
            productRecycle.adapter = adapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAddressBottomSheet() {
        val addressList = ArrayList<AddressModel>()
        addressList.addAll(AddressStorage(this).getAddressList() ?: arrayListOf())
        val adapter = AddressAdapter(this@AddressConfirmActivityV2, addressList, "createNewAddress", this)
        val bottomSheetBinding = BottomSheetAddressBinding.inflate(LayoutInflater.from(this))
        val bottomSheetView = bottomSheetBinding.root
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetBinding.apply {
            addNewAddress.setOnClickListener {
                showAddressInfoBottomSheet(adapter)
            }
            addressRecycler.setHasFixedSize(true)
            addressRecycler.layoutManager = LinearLayoutManager(this@AddressConfirmActivityV2, LinearLayoutManager.VERTICAL, false)
            addressRecycler.adapter = adapter
        }

        bottomSheetDialog.setOnDismissListener {
            val selectedAddress = addressList.find { it.selected == true }
            address = selectedAddress
            binding.addressBtn.text = selectedAddress?.street + " " + selectedAddress?.house
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
                    AddressStorage(this@AddressConfirmActivityV2).saveAddress(myAddress)
                    bottomSheetDialog.dismiss()
                    adapter.updateList()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        val addressList = AddressStorage(this).getAddressList() ?: arrayListOf()
        binding.apply {
            addressBtn.setOnClickListener{
                showAddressBottomSheet()
            }
            val selectedAddress = addressList.find { it.selected == true }
            addressBtn.text = selectedAddress?.street + " " + selectedAddress?.house
            address = selectedAddress

            val bundle: Bundle? = intent.extras
            val orderPriceIn  = bundle?.getDouble("orderPrice") ?: 0.0
            val getDeliveryPrice = bundle?.getString("deliveryPrice")

            orderPrice.text = "${String.format("%.2f", orderPriceIn)} сомони"
//            deliveryPrice.text = "$getDeliveryPrice сомони"
//            val countTotalPrice = (orderPriceIn ?: 0.0) + (getDeliveryPrice ?: "0.0").toDouble()
//            totalPrice.text = "${String.format("%.2f", countTotalPrice)} сомони"
            if (deliveryWayText.text.toString() == "Стандартная доставка (до 2х часов)"){
                deliveryPrice.text = "$getDeliveryPrice сомони"
                val countTotalPriceIn = (orderPriceIn ?: 0.0) + (getDeliveryPrice ?: "0.0").toDouble()
                totalPrice.text = "${String.format("%.2f", countTotalPriceIn)} сомони"
            }else if(deliveryWayText.text.toString() == "Экспресс доставка (до 1 часа)"){
                deliveryPrice.text = "50 сомони"
                val countTotalPriceIn = (orderPriceIn ?: 0.0) + "50.0".toDouble()
                totalPrice.text = "${String.format("%.2f", countTotalPriceIn)} сомони"
            }
            else{
                deliveryPrice.text = "0 сомони"
                totalPrice.text = "${String.format("%.2f", orderPriceIn)} сомони"
            }

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR)
            val minutes = c.get(Calendar.MINUTE)
            var dayWith0 = ""
            var monthWith0 = ""
            var hourWith0 = ""
            var minuteWith0 = ""
            var selectedDate = ""
            var selectedTime = ""

            dateTime.setOnClickListener{
                TimePickerDialog(this@AddressConfirmActivityV2, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    hourWith0 = if (hourOfDay<10){
                        "0$hourOfDay"
                    }else{
                        "$hourOfDay"
                    }
                    minuteWith0 = if (minute<10){
                        "0$minute"
                    }else{
                        "$minute"
                    }
                    if (selectedDate!=""){
                        selectedTime = "$hourWith0:$minuteWith0"
                        dateTime.text = "Время доставки: $selectedDate в $hourWith0:$minuteWith0".toEditable()
                    }
                }, hour, minutes, true).show()

                DatePickerDialog(this@AddressConfirmActivityV2, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                    dayWith0 = if (mday<10){
                        "0$mday"
                    }else{
                        "$mday"
                    }
                    monthWith0 = if (mmonth<9){
                        "0${mmonth + 1}"
                    }else{
                        "${mmonth + 1}"
                    }
                    selectedDate = "$dayWith0.$monthWith0.$myear"
                }, year, month, day).show()
            }

            val itemList = listOf("Наличные", "Банковская карта", "Электронный кошелёк - alif mobi")
            val itemList2 = listOf("Стандартная доставка (до 2х часов)", "Экспресс доставка (до 1 часа)", "Самовывоз (до 2х часов)")
            val adapter = ArrayAdapter(this@AddressConfirmActivityV2, R.layout.list_item, itemList)
            val adapter2 = ArrayAdapter(this@AddressConfirmActivityV2, R.layout.list_item, itemList2)
            (payWayLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            (deliveryWayLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter2)

            (deliveryWayLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, _, _ ->
                if (deliveryWayText.text.toString() == "Стандартная доставка (до 2х часов)"){
                    deliveryPrice.text = "$getDeliveryPrice сомони"
                    val countTotalPriceIn = (orderPriceIn ?: 0.0) + (getDeliveryPrice ?: "0.0").toDouble()
                    totalPrice.text = "${String.format("%.2f", countTotalPriceIn)} сомони"
                }else if(deliveryWayText.text.toString() == "Экспресс доставка (до 1 часа)"){
                    deliveryPrice.text = "50 сомони"
                    val countTotalPriceIn = (orderPriceIn ?: 0.0) + "50.0".toDouble()
                    totalPrice.text = "${String.format("%.2f", countTotalPriceIn)} сомони"
                }
                else{
                    deliveryPrice.text = "0 сомони"
                    totalPrice.text = "${String.format("%.2f", orderPriceIn)} сомони"
                }
            }

            //Оформить заказ
            resume.setOnClickListener {
                if (address != null){
                    if (orderPriceIn < 100.0){
                        MaterialAlertDialogBuilder(this@AddressConfirmActivityV2)
                            .setMessage("Минимальная сумма заказа 100 сомони")
                            .setPositiveButton("Продолжить"){_,_ ->}
                            .show()
                    }else{
                        val intent = Intent(this@AddressConfirmActivityV2, ConfirmOrderActivity::class.java)
                        intent.putExtra("deliveryAddress", "${address!!.street}, ${address!!.house}, Подъезд ${address!!.entrance}, кв ${address!!.flat}, этаж ${address!!.floor}")
                        intent.putExtra("comment", "${commentText.text.toString()}, способ доставки: ${deliveryWayText.text}, способ оплаты: ${payWayText.text}, заказ сделан с платформы Android")
                        intent.putExtra("getDate", selectedDate)
                        intent.putExtra("getTime", selectedTime)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    override fun onSelected(address: AddressModel) {
        bottomSheetDialog.dismiss()
        this.address = address
        setupView()
    }
}