package tj.paykar.shop.presentation.card.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityCardHistoryDetailsBinding
import tj.paykar.shop.domain.adapter.card.HistoryProductCardAdapter
import tj.paykar.shop.domain.usecase.MainManagerService

class CardHistoryDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCardHistoryDetailsBinding
    val adapter = HistoryProductCardAdapter(this)

    var createDate: String = ""
    var addBonus: Double = 0.0
    var removeBonus: Double = 0.0
    var totalPrice: Double = 0.0
    var totalPriceDiscount: Double = 0.0
    var fiscalCode: String = ""
    var cash: String = ""
    var payment: String = ""
    var address: String = ""
    var addChip: Int = 0
    var removeChip: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardHistoryDetailsBinding.inflate(layoutInflater)
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
        setupAdapter()
        service()

    }

    private fun service() {
        val productList = UserStorageData(this).getCardHistoryProduct() ?: ArrayList()
        adapter.updateProductList(productList)
    }

    private fun setupAdapter() {
        binding.apply {
            productListCard.setHasFixedSize(true)
            productListCard.layoutManager = LinearLayoutManager(this@CardHistoryDetailsActivity, LinearLayoutManager.VERTICAL, false)
            productListCard.adapter = adapter
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun setupView() {
        binding.apply {

            dismiss.setColorFilter(resources.getColor(R.color.shopGrey))
            dismiss.setOnClickListener { finish() }

            val convertDate = MainManagerService().dateConvert(createDate)
            val number = cash.split(" ")
            val cashNumber = try {
                number[1]
            }catch (_:Exception){
                ""
            }
            val benefit = totalPrice - totalPriceDiscount
            val roundedBenefit = String.format("%.2f", benefit)
            dateCard.text = convertDate
            checkNumber.text = "№ чека: $fiscalCode"
            checkTicketDetail.text = "№ кассы: $cashNumber"
            checkTotalInDetail.text = "$totalPriceDiscount сомони"
            checkOffsScoresInDetail.text = "-$removeBonus баллов"
            checkPaymentTypeDetail.text = payment
            checkYouBenefitInDetail.text = "$roundedBenefit сомони"
            checkAccruedInDetail.text = "+$addBonus баллов"
            pointMarketCard.text = address

            var starText = "звезда"

            if (addChip in 4 downTo 2) {
                starText = "звезды"
            } else if(addChip >= 5) {
                starText = "звезд"
            }

            if (addChip != 0) {
                addInStar.text = "$addChip $starText"
            } else {
                addInStar.text = "0 звезд"
            }

            if (removeChip in 4 downTo 2) {
                starText = "звезды"
            } else if(removeChip >= 5) {
                starText = "звезд"
            }

            if (removeChip != 0) {
                addStar.setTextColor(getResources().getColor(R.color.red))
                addInStar.setTextColor(getResources().getColor(R.color.red))
                addStar.text = "Списано звезд"
                addInStar.text = "$removeChip $starText"
            }
        }
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        createDate = bundle?.getString("createDate") ?: ""
        addBonus = bundle?.getDouble("addBonus") ?: 0.0
        removeBonus = bundle?.getDouble("removeBonus") ?: 0.0
        totalPrice = bundle?.getDouble("totalPrice") ?: 0.0
        totalPriceDiscount = bundle?.getDouble("totalPriceDiscount") ?: 0.0
        fiscalCode = bundle?.getString("fiscalCode") ?: ""
        cash = bundle?.getString("cashNumber") ?: ""
        payment = bundle?.getString("payment") ?: ""
        address = bundle?.getString("address") ?: ""
        addChip = bundle?.getInt("addChip") ?: 0
        removeChip = bundle?.getInt("removeChip") ?: 0
    }


}