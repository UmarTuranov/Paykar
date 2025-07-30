package tj.paykar.shop.presentation.shop.basket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import tj.paykar.shop.data.model.shop.AddressModel
import tj.paykar.shop.data.storage.AddressStorage
import tj.paykar.shop.databinding.ActivityChequeListBinding
import tj.paykar.shop.databinding.BottomSheetAddressBinding
import tj.paykar.shop.databinding.BottomSheetAddressInfoBinding
import tj.paykar.shop.domain.adapter.address.AddressAdapter
import tj.paykar.shop.domain.adapter.basket.ChequeListAdapter
import tj.paykar.shop.domain.reprository.shop.AddressSelectedManager

class ChequeListActivity : AppCompatActivity(),
    tj.paykar.shop.domain.reprository.shop.AddressSelectedManager {

    private var openMap: Boolean = false

    lateinit var binding: ActivityChequeListBinding
    private val adapter = ChequeListAdapter(this)

    private var orderPriceIn: Double = 0.0
    private var discountPriceIn: String = "0"
    private var deliveryPriceIn: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChequeListBinding.inflate(layoutInflater)
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

        setupAdapter()


    }

    @SuppressLint("SetTextI18n")
    private fun setupAdapter() {

        binding.apply {

            basket.setHasFixedSize(true)
            basket.layoutManager =
                LinearLayoutManager(this@ChequeListActivity, LinearLayoutManager.VERTICAL, false)
            basket.adapter = adapter
            adapter.updateData()

            //orderTitlr.textSize = 22F

            orderPrice.text = "${String.format("%.2f", orderPriceIn)} сомони"

            resume.setOnClickListener {
                val addressList = AddressStorage(this@ChequeListActivity).getAddressList() ?: arrayListOf()
                if (addressList.isNotEmpty()){
                    val intent = Intent(this@ChequeListActivity, AddressConfirmActivityV2::class.java)
                    intent.putExtra("deliveryPrice", deliveryPriceIn)
                    intent.putExtra("orderPrice", orderPriceIn)
                    startActivity(intent)
                }else{
                    showAddressBottomSheet()
                }
            }
        }

    }

    private fun showAddressBottomSheet() {
        val addressList = ArrayList<AddressModel>()
        addressList.addAll(AddressStorage(this).getAddressList() ?: arrayListOf())
        val adapter = AddressAdapter(this@ChequeListActivity, addressList, "createNewAddress", this)
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
            addressRecycler.layoutManager = LinearLayoutManager(this@ChequeListActivity, LinearLayoutManager.VERTICAL, false)
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
                    AddressStorage(this@ChequeListActivity).saveAddress(myAddress)
                    bottomSheetDialog.dismiss()
                    adapter.updateList()
                }
            }
        }
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        orderPriceIn = bundle?.getDouble("orderPrice") ?: 0.0
        discountPriceIn = bundle?.getString("discountPrice") ?: ""
        deliveryPriceIn = bundle?.getString("deliveryPrice") ?: ""
    }

    override fun onSelected(address: AddressModel) {}
}