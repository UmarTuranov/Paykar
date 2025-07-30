package tj.paykar.shop.domain.adapter.gift

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.gift.DeferredGiftListModel
import tj.paykar.shop.data.model.gift.GiftModel
import tj.paykar.shop.databinding.BottomSheetGiftInfoBinding
import tj.paykar.shop.databinding.ItemDeferredGiftBinding
import tj.paykar.shop.domain.usecase.GiftManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog

class DeferredGiftAdapter(private var giftList: ArrayList<DeferredGiftListModel>, private val context: Context): RecyclerView.Adapter<DeferredGiftAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemDeferredGiftBinding.bind(view)
        private val bottomSheet = BottomSheetDialog(context)
        private val bottomSheetBinding = BottomSheetGiftInfoBinding.inflate(LayoutInflater.from(context))

        fun bind(gift: DeferredGiftListModel) = with(binding) {
            Glide.with(context)
                .load("https://admin.paykar.tj/upload/gift/media/"+gift.giftDetails.image)
                .into(giftImage)

            giftTitle.text = gift.giftDetails.title

            useButton.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Внимание")
                    .setMessage("При нажатии на кнопку Использовать ваш шанс будет аннулирован. Пожалуйста, убедитесь, что хотите продолжить.")
                    .setPositiveButton("Использовать") {_, _ ->
                        useGift()
                    }
                    .setNegativeButton("Отмена") {_, _ ->}
                    .show()
            }

            itemView.setOnClickListener {
                bottomSheet.setContentView(bottomSheetBinding.root)
                bottomSheet.show()

                Glide.with(context)
                    .load("https://admin.paykar.tj/upload/gift/media/" + gift.giftDetails.image)
                    .into(bottomSheetBinding.giftImage)

                bottomSheetBinding.giftTitle.text = gift.giftDetails.title ?: ""
                bottomSheetBinding.giftDescription.text = gift.giftDetails.description ?: ""

                bottomSheetBinding.useButton.setOnClickListener {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Внимание")
                        .setMessage("При нажатии на кнопку Использовать ваш шанс будет аннулирован. Пожалуйста, убедитесь, что хотите продолжить.")
                        .setPositiveButton("Использовать") {_, _ ->
                            useGift()
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                }

                bottomSheetBinding.cancelButton.setOnClickListener {
                    bottomSheet.dismiss()
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun useGift() {
            val progressDialog = CustomProgressDialog(context)
            val isOnline = MainManagerService().internetConnection(context)
            if (!isOnline) {
                tj.paykar.shop.domain.usecase.wallet.MainManagerService().noInternetAlert(context)
            } else {
                progressDialog.show()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = GiftManagerService().receiveGift(
                            giftList[adapterPosition].deferred.phone ?: "",
                            giftList[adapterPosition].deferred.card_code ?: "",
                            giftList[adapterPosition].deferred.unit ?: "",
                            giftList[adapterPosition].deferred.user_id ?: 0,
                            giftList[adapterPosition].deferred.gift_id ?: 0,
                            giftList[adapterPosition].deferred.full_name ?: "",
                            giftList[adapterPosition].deferred.category_gift ?: "",
                            giftList[adapterPosition].deferred.sum ?: 0.0,
                            giftList[adapterPosition].deferred.number_check ?: "",
                            "deferred"
                        )
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            if (request.isSuccessful) {
                                val response = request.body()
                                Log.d("--RequestInfo", response.toString())
                                if (response?.status == "success") {
                                    giftList = response.deferred ?: arrayListOf()
                                    bottomSheet.dismiss()
                                    notifyItemRemoved(adapterPosition)
                                } else {
                                    MaterialAlertDialogBuilder(context)
                                        .setTitle("Произошла ошибка")
                                        .setMessage("Что то пошло не так, повторите попытку ещё раз! Код ошибки: 500")
                                        .setPositiveButton("Понятно") {_, _ ->}
                                        .show()
                                }
                            } else {
                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Произошла ошибка")
                                    .setMessage("Что то пошло не так, повторите попытку ещё раз! Код ошибки: 1000")
                                    .setPositiveButton("Понятно") {_, _ ->}
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("--Exception", e.toString())
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Произошла ошибка")
                                .setMessage("Что то пошло не так, повторите попытку ещё раз!")
                                .setPositiveButton("Понятно") {_, _ ->}
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_deferred_gift, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return giftList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(giftList[position])
    }
}