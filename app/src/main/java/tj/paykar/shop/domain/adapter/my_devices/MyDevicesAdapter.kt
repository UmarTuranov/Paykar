package tj.paykar.shop.domain.adapter.my_devices

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.MyDevicesModel
import tj.paykar.shop.databinding.ItemDeviceModelLayoutBinding

class MyDevicesAdapter(val context: Context,val devicesList: ArrayList<MyDevicesModel>, val myDevice: String): RecyclerView.Adapter<MyDevicesAdapter.MyDevicesViewHolder>() {

    init {
        moveDeviceToTop()
    }

    inner class MyDevicesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemDeviceModelLayoutBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(device: MyDevicesModel) = with(binding) {
            val colors = intArrayOf(
                context.resources.getColor(R.color.white),
            )
            val white = ColorStateList(arrayOf(IntArray(0)), intArrayOf(colors[0]))
            if (device.device_model == myDevice){
                deviceItem.setBackgroundColor(context.resources.getColor(R.color.paykar))
                mobileIcon.imageTintList = white
                deviceModelName.setTextColor(ContextCompat.getColor(context, R.color.white))
                versionOS.setTextColor(ContextCompat.getColor(context, R.color.white))
                thisDevice.isVisible = true
            }
            deviceModelName.text = device.device_model
            versionOS.text = "${device.type_os} ${device.version_os}"
        }
    }

    private fun moveDeviceToTop() {
        val index = devicesList.indexOfFirst { it.device_model == myDevice }
        if (index > 0) {
            val device = devicesList.removeAt(index)
            devicesList.add(0, device)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDevicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_device_model_layout, parent, false)
        return MyDevicesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyDevicesViewHolder, position: Int) {
        return holder.bind(devicesList[position])
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }
}