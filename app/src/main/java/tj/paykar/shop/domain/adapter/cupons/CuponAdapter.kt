package tj.paykar.shop.domain.adapter.cupons

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.loyalty.CuponModel
import tj.paykar.shop.databinding.ItemCuponsBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.profile.сupons.CuponDetailActivity

class CuponAdapter: RecyclerView.Adapter<CuponAdapter.CuponHoder>() {

    var cuponList = ArrayList<CuponModel>()
    private lateinit var context: Context

    class CuponHoder(item: View): RecyclerView.ViewHolder(item) {

        val biding = ItemCuponsBinding.bind(item)

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(cupon: CuponModel, context: Context) = with(biding) {
            title.text = cupon.programName
            val dateConvert = MainManagerService().dateToConvert(cupon.dateFinish)
            date.text = "Действует до: $dateConvert"

//            Glide.with(context)
//                 .load("https://paykar.cloud39.ru" + cupon.previewImage)
//                 .centerCrop()
//                 .placeholder(R.drawable.nophoto)
//                 .into(previewImageContent)

            //

            link.setOnClickListener {
                val intent = Intent(context, CuponDetailActivity::class.java)
                intent.putExtra("cuponName", cupon.programName)
                intent.putExtra("cuponDate", cupon.dateFinish)
                intent.putExtra("cuponCode", cupon.barcode)
                intent.putExtra("cuponDescription", cupon.programDescription)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuponHoder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cupons, parent, false)
        return CuponHoder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CuponHoder, position: Int) {
        return holder.bind(cuponList[position], context)
    }

    override fun getItemCount(): Int {
        return cuponList.size
    }


}