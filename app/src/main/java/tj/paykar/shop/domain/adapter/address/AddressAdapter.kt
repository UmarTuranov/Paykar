package tj.paykar.shop.domain.adapter.address

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.AddressModel
import tj.paykar.shop.data.storage.AddressStorage
import tj.paykar.shop.databinding.AddressItemLayoutBinding
import tj.paykar.shop.databinding.BottomSheetAddressInfoBinding
import tj.paykar.shop.domain.reprository.shop.AddressSelectedManager

class AddressAdapter(context: Context, var addressList: ArrayList<AddressModel>, private val reason: String, private val onSelect: tj.paykar.shop.domain.reprository.shop.AddressSelectedManager): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    var mContext = context
    @SuppressLint("NotifyDataSetChanged")
    inner class AddressViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = AddressItemLayoutBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(addressModel: AddressModel) = with(binding){
            address.text = addressModel.street + " " + addressModel.house
            isChecked.isVisible = addressModel.selected!!
            edit.isVisible = addressModel.selected!!
        }

        init {
            view.setOnClickListener{
                val position: Int = adapterPosition
                if (addressList[position].selected!!){
                    val bottomSheetDialog = BottomSheetDialog(mContext)
                    val bottomSheetBinding = BottomSheetAddressInfoBinding.inflate(LayoutInflater.from(mContext))
                    val bottomSheetView = bottomSheetBinding.root
                    bottomSheetDialog.setContentView(bottomSheetView)
                    bottomSheetDialog.show()
                    bottomSheetBinding.apply {
                        streetText.text = (addressList[position].street ?: "").toEditable()
                        houseText.text = (addressList[position].house ?: "").toEditable()
                        entranceText.text = (addressList[position].entrance ?: "").toEditable()
                        flatText.text = (addressList[position].flat ?: "").toEditable()
                        floorText.text = (addressList[position].floor ?: "").toEditable()

                        delete.isVisible = true
                        saveBtn.text = "Обновить"

                        saveBtn.setOnClickListener {
                            val updatedAddress = AddressModel(
                                streetText.text.toString(),
                                houseText.text.toString(),
                                entranceText.text.toString(),
                                flatText.text.toString(),
                                floorText.text.toString(),
                                addressList[position].selected
                            )
                            addressList[position] = updatedAddress
                            AddressStorage(mContext).saveAddressList(addressList)
                            bottomSheetDialog.dismiss()
                            updateList()
                        }

                        delete.setOnClickListener {
                            MaterialAlertDialogBuilder(mContext)
                                .setTitle("Удалить адрес?")
                                .setMessage("Вы уверены что хотите удалить этот адрес")
                                .setPositiveButton("Да"){_,_ ->
                                    addressList.removeAt(position)
                                    try {
                                        addressList.last().selected = true
                                    } catch (_: Exception){}
                                    AddressStorage(mContext).saveAddressList(addressList)
                                    bottomSheetDialog.dismiss()
                                    updateList()
                                }
                                .setNegativeButton("Отменить"){_,_ ->}
                                .show()
                        }
                    }
                }else{
                    for (i in 0 until addressList.size){
                        addressList[i].selected = false
                    }
                    addressList[position].selected = true
                    AddressStorage(mContext).saveAddressList(addressList)
                    notifyDataSetChanged()
                    onSelect.onSelected(addressList[position])
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.address_item_layout, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        return holder.bind(addressList[position])
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList() {
        val addressList = AddressStorage(mContext).getAddressList()
        this.addressList = (addressList ?: emptyList()) as ArrayList<AddressModel>
        notifyDataSetChanged()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}