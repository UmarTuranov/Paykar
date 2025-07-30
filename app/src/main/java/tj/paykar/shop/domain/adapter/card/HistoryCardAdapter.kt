package tj.paykar.shop.domain.adapter.card

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.loyalty.HistoryCardModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ItemCardHistoryBinding
import tj.paykar.shop.domain.usecase.CardManagerService
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.presentation.card.history.CardHistoryDetailsActivity

class HistoryCardAdapter (context: Context): RecyclerView.Adapter<HistoryCardAdapter.HistoryHolder>() {

    var historyList = ArrayList<HistoryCardModel>()
    var mContext = context

    inner class HistoryHolder(view: View, context: Context, historyList: ArrayList<HistoryCardModel>): RecyclerView.ViewHolder(view) {
        val biding = ItemCardHistoryBinding.bind(view)

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(history: HistoryCardModel) = with(biding) {

            val convertDate = MainManagerService().dateConvert(history.DocumentDate)
            purchaseDateCard.text = convertDate
            purchaseAmountCard.text = "${history.TotalSumDiscounted} сомони"
            purchaseAddBonusCard.text = "+${history.AddBonus} баллов"
            if (history.DiscountPercent != 0.0) {
                percentDiscount.text = "-${history.DiscountPercent}%"
            } else {
                percentDiscount.text = ""
            }
            if (history.RemoveBonus != 0.0) {
                offsCard.text = "-${history.RemoveBonus} баллов"
            } else {
                offsCard.text = ""
            }
            //val benefit = history.RemoveBonus / 10
            val benefit = (history.TotalSum - history.TotalSumDiscounted) + (history.AddBonus / 10)
            val roundedBenefit = String.format("%.2f", benefit)
            benefitCard.text = "Ваша выгода $roundedBenefit сомони"
            pointMarketCard.text = history.SubjectFullAdress

            star1.isVisible = false
            star2.isVisible = false
            star3.isVisible = false
            star4.isVisible = false
            star5.isVisible = false

            if (history.AddChips != 0) {
                star1.setColorFilter(mContext.getResources().getColor(R.color.yellow))
                star2.setColorFilter(mContext.getResources().getColor(R.color.yellow))
                star3.setColorFilter(mContext.getResources().getColor(R.color.yellow))
                star4.setColorFilter(mContext.getResources().getColor(R.color.yellow))
                star5.setColorFilter(mContext.getResources().getColor(R.color.yellow))
            }

            if (history.AddChips == 5) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
                star4.isVisible = true
                star5.isVisible = true
            } else if (history.AddChips == 4) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
                star4.isVisible = true
            } else if (history.AddChips == 3) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
            } else if (history.AddChips == 2) {
                star1.isVisible = true
                star2.isVisible = true
            } else if (history.AddChips == 1) {
                star1.isVisible = true
            } else if (history.AddChips > 5) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
                star4.isVisible = true
                star5.isVisible = true
            }

            if (history.RemoveChips != 0) {
                star1.setColorFilter(mContext.getResources().getColor(R.color.red))
                star2.setColorFilter(mContext.getResources().getColor(R.color.red))
                star3.setColorFilter(mContext.getResources().getColor(R.color.red))
                star4.setColorFilter(mContext.getResources().getColor(R.color.red))
                star5.setColorFilter(mContext.getResources().getColor(R.color.red))
            }

            if (history.RemoveChips == 5) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
                star4.isVisible = true
                star5.isVisible = true
            } else if (history.RemoveChips == 4) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
                star4.isVisible = true
            } else if (history.RemoveChips == 3) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
            } else if (history.RemoveChips == 2) {
                star1.isVisible = true
                star2.isVisible = true
            } else if (history.RemoveChips == 1) {
                star1.isVisible = true
            } else if (history.RemoveChips > 5) {
                star1.isVisible = true
                star2.isVisible = true
                star3.isVisible = true
                star4.isVisible = true
                star5.isVisible = true
            }

            cardViewHistory.setOnClickListener {
                val position: Int = adapterPosition
                UserStorageData(mContext).saveCardHistoryProduct(historyList[position].products)
                val intent = Intent(mContext, CardHistoryDetailsActivity::class.java)
                intent.putExtra("createDate", historyList[position].CreateDate)
                intent.putExtra("addBonus", historyList[position].AddBonus)
                intent.putExtra("removeBonus", historyList[position].RemoveBonus)
                intent.putExtra("totalPrice", historyList[position].TotalSum)
                intent.putExtra("totalPriceDiscount", historyList[position].TotalSumDiscounted)
                intent.putExtra("fiscalCode", historyList[position].DocumentFiscalCode)
                intent.putExtra("cashNumber", historyList[position].CashName)
                intent.putExtra("payment", historyList[position].PaymentForm)
                intent.putExtra("address", historyList[position].SubjectFullAdress)
                intent.putExtra("addChip", historyList[position].AddChips)
                intent.putExtra("removeChip", historyList[position].RemoveChips)
                mContext.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_card_history, parent, false)
        return HistoryHolder(view, mContext, historyList)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        return holder.bind(historyList[position])
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    suspend fun sendRequest(token: String) {
        try {
            val sendReq = CardManagerService().historyCardList(token)
            val response: ArrayList<HistoryCardModel> = sendReq
            this.historyList = response
        }catch (_:Exception){}
    }

    fun updateHistory(history: ArrayList<HistoryCardModel>) {
        this.historyList = history
    }
}