//package tj.paykar.shop.presentation.prizes
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.WindowManager
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import tj.paykar.shop.R
//import tj.paykar.shop.data.model.CheckForGiftModel
//import tj.paykar.shop.databinding.ActivityPrizesBinding
//import tj.paykar.shop.domain.reprository.shop.PrizesItemClickManager
//
//class PrizesActivity : AppCompatActivity(), PrizesItemClickManager {
//    lateinit var binding: ActivityPrizesBinding
//    private lateinit var prizesModel: CheckForGiftModel
//    private var gift: GiftModel? = null
//    lateinit var bottomSheetGift: BottomSheetDialog
//    lateinit var bottomSheetGiftView: View
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPrizesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        bottomSheetGift = BottomSheetDialog(
//            this, R.style.BottomSheetDialogTheme
//        )
//        bottomSheetGiftView = LayoutInflater.from(this).inflate(
//            R.layout.bottom_sheet_gift,
//            findViewById(R.id.giftBottomSheet)
//        )
//        bottomSheetGift.setContentView(bottomSheetGiftView)
//        bottomSheetGift.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
//
//
//        getPutIntent()
//     //   setupView()
//    }
//
//    private fun getPutIntent(){
//        val bundle: Bundle? = intent.extras
//        prizesModel = bundle?.getSerializable("prizesModel") as CheckForGiftModel
//        Log.d("--P Prizes", prizesModel.toString())
//    }
//
////    private fun setupView(){
////        binding.apply {
////            title.text = prizesModel.title
////            description.text = prizesModel.description
////
////            val adapter = PrizesAdapter(this@PrizesActivity, prizesModel.giftList ?: arrayListOf() , this@PrizesActivity)
////            giftsRV.setHasFixedSize(true)
////            giftsRV.layoutManager = GridLayoutManager(this@PrizesActivity, 3)
////            giftsRV.adapter = adapter
////
//////            val giftName: TextView = bottomSheetGiftView.findViewById(R.id.giftName)
//////            val giftPicture: ImageView = bottomSheetGiftView.findViewById(R.id.giftPicture)
//////            val finishBS: MaterialButton = bottomSheetGiftView.findViewById(R.id.finishBS)
////
////            getGiftBtn.setOnClickListener {
////                val userInfo = UserStorageData(this@PrizesActivity).getInfoCard()
////                val userIdForHomePageService = UserStorageData(this@PrizesActivity).getUserId()
////                val dialog = MaterialAlertDialogBuilder(this@PrizesActivity)
////                    .setTitle("Внимание!")
////                    .setMessage("При нажатии на кнопку \"Получить подарок\" вне кассовой зоны вы потеряете шанс его получить.")
////                    .setCancelable(false)
////                    .setNegativeButton("Отложить"){_,_ ->
////                        gift?.purchaseId = prizesModel.purchaseId
////                        UserStorageData(this@PrizesActivity).saveGift(gift!!)
////                        startActivity(Intent(this@PrizesActivity, MainActivity::class.java))
////                    }
////                    .setPositiveButton("Получить"){_,_ ->
////                        CoroutineScope(Dispatchers.IO).launch {
////                            try {
////                                ReceiveGiftManagerService().receiveGift(
////                                    userInfo.phone ?: "",
////                                    userInfo.cardCode ?: "",
////                                    prizesModel.unit ?: "",
////                                    userIdForHomePageService,
////                                    gift?.id ?: 0,
////                                    prizesModel.purchaseId
////                                )
////                            }catch (e:Exception){
////                                Log.d("--E Error", e.toString())
////                            }
////                        }
////                        giftName.text = gift!!.name
////                        Glide.with(this@PrizesActivity)
////                            .load("https://mobileapp.paykar.tj/api/gifts/images/" + gift!!.image)
////                            .centerCrop()
////                            .placeholder(R.drawable.nophoto)
////                            .into(giftPicture)
////
////                        finishBS.setOnClickListener {
////                            startActivity(Intent(this@PrizesActivity, MainActivity::class.java))
////                        }
////                        bottomSheetGift.setCancelable(false)
////                        bottomSheetGift.show()
////                    }
////                    .show()
////
////                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.red))
////            }
////        }
////    }
//
//    @SuppressLint("SetTextI18n")
//    override fun give(position: Int) {
//        binding.getGiftBtn.isEnabled = true
//        binding.giftNameText.text = "Ваш подарок ${prizesModel.giftList?.get(position)?.name}"
//        gift = prizesModel.giftList?.get(position)
//    }
//}