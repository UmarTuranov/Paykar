package tj.paykar.shop.presentation.profile.сupons

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import tj.paykar.shop.data.model.loyalty.CuponModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityCuponBinding
import tj.paykar.shop.domain.adapter.cupons.CuponAdapter

class CuponActivity : AppCompatActivity() {

    lateinit var binding: ActivityCuponBinding
    private val adapter = CuponAdapter()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            binding = ActivityCuponBinding.inflate(layoutInflater)
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
            setupView()
            setupAdaper()
            service()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun service() {
        val couponList = ArrayList<CuponModel>()
            val contentList = UserStorageData(this).getPromoList()
            val cardList = UserStorageData(this).getCardList()
            if (cardList != null) {
                for (card in cardList) {
                    if (contentList != null) {
                        for (content in contentList){
                            if (card.CreateByMarketProgramId == content.MarketProgramId && !card.Blocked) {
                                var programName = ""
                                var programDescription = ""
                                var previewImage = ""
                                val dateStart = card.StartDate
                                val dateFinish = card.FinishDate
                                val promoCode = card.CardCode
                                for (coupon in content.ChildrenConentFields) {
                                    val name = coupon.TypeDisplayName
                                    val value = coupon.Value
                                    if(name == "Название") {
                                        programName = value
                                    } else if (name == "Текст анонса") {
                                        programDescription = value
                                    } else if (name == "Изображение анонса") {
                                        previewImage = value
                                    }
                                }
                                val myCoupon = CuponModel(programName, programDescription, previewImage, dateStart, dateFinish, promoCode)
                                couponList.add(myCoupon)
                            }
                        }
                    }
                }
            }

        adapter.cuponList = couponList
        adapter.notifyDataSetChanged()
    }

    private fun setupAdaper() {
        binding.apply {
            cupons.setHasFixedSize(true)
            cupons.layoutManager = LinearLayoutManager(this@CuponActivity, LinearLayoutManager.VERTICAL, false)
            cupons.adapter = adapter
        }
    }

    private fun setupView() {
        binding.apply {

        }
    }
}