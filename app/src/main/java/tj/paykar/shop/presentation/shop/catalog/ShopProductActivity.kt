package tj.paykar.shop.presentation.shop.catalog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.AddProductReviewModel
import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.data.model.shop.MyBasketsProductModel
import tj.paykar.shop.data.model.shop.ProductReviewModel
import tj.paykar.shop.data.storage.MyBasketStorage
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityCatalogProductBinding
import tj.paykar.shop.domain.adapter.basket.MyBasketsListAdapter
import tj.paykar.shop.domain.adapter.catalog.product.Product3ReviewsAdapter
import tj.paykar.shop.domain.adapter.catalog.product.ProductLikeAdapter
import tj.paykar.shop.domain.adapter.catalog.product.ProductReviewsAdapter
import tj.paykar.shop.domain.usecase.shop.AddProductReviewManagerService
import tj.paykar.shop.domain.usecase.shop.ProductLikeManagerService
import tj.paykar.shop.domain.usecase.shop.WishlistManagerService
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.domain.usecase.wallet.AnimateViewHeight
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import tj.paykar.shop.presentation.authorization.LoginActivity
import tj.paykar.shop.presentation.shop.basket.BasketActivity
import tj.paykar.shop.presentation.shop.basket.MyBasketsActivity
import tj.paykar.shop.presentation.shop.wishlist.WishlistActivity
import java.math.RoundingMode

class ShopProductActivity : AppCompatActivity(),
    tj.paykar.shop.domain.reprository.shop.SeeMoreReviewsListener {

    lateinit var binding: ActivityCatalogProductBinding
    var myBasketList: ArrayList<MyBasketModel> = arrayListOf()
    var product: MyBasketsProductModel = MyBasketsProductModel("", "", "", "")
    private var userId: Int = 0
    private var productId: String = ""
    private var productNameIn: String = ""
    private var productImageIn: String = ""
    private var productPriceIn: String = ""
    private var productQuan: Double = 0.0
    private var productUnit: String = ""
    private var nutritional: String = ""
    private var composition: String = ""
    private var termConditions: String = ""
    private var manufacturer: String = ""
    private var productWishlist: Boolean = false
    private var productDetail: String = ""
    private var productInMyBaskets: Boolean = false
    private var basketHaveProductName: String = ""
    var arrayOfIndex = ArrayList<Int>()
    var reviewList = ArrayList<ProductReviewModel>()

    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetView: View
    lateinit var addReviewBottomSheetDialog: BottomSheetDialog
    lateinit var addReviewBottomSheetView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogProductBinding.inflate(layoutInflater)
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
        userId = UserStorageData(this).getUserId()
        myBasketList = MyBasketStorage(this@ShopProductActivity).getBasketLists()
        getPutIntent()

        bottomSheetDialog = BottomSheetDialog(
            this, R.style.BottomSheetDialogTheme
        )
        bottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_product_reviews,
            findViewById(R.id.productReviewsBS)
        )
        bottomSheetDialog.setContentView(bottomSheetView)
        window.navigationBarColor = this.resources.getColor(R.color.whiteToBlack)
        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

        //-------------------------------------------------------------

        addReviewBottomSheetDialog = BottomSheetDialog(
            this, R.style.BottomSheetDialogTheme
        )
        addReviewBottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_add_product_review,
            findViewById(R.id.addProductReviewBS)
        )
        addReviewBottomSheetDialog.setContentView(addReviewBottomSheetView)
        addReviewBottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            animateCardRadiusDirectly(binding.footerView, scrollY)
        }

        settingView()
    }

    private fun animateCardRadiusDirectly(cardView: CardView, scrollY: Int) {
        val maxRadius = 90F
        val minRadius = 0F
        val maxScroll = 400
        Log.d("--S ScrollY", scrollY.toString())
        val newRadius = minRadius + (maxRadius - minRadius) * (scrollY.toFloat() / maxScroll.toFloat()).coerceIn(0f, 1f)
        cardView.radius = newRadius
    }

    private fun getPutIntent() {
        val bundle: Bundle? = intent.extras
        productId = bundle?.getString("productId") ?: ""
        productNameIn = bundle?.getString("productName") ?: ""
        productImageIn = bundle?.getString("productImage") ?: ""
        productPriceIn = bundle?.getString("productPrice") ?: ""
        productQuan = bundle?.getDouble("productQuan") ?: 0.0
        productUnit = bundle?.getString("productUnit") ?: "шт"
        binding.unit.text = "Цена за 1 $productUnit"

        nutritional = bundle?.getString("nutritional") ?: ""
        binding.nutritionalTitle.isVisible = nutritional != ""
        binding.nutritionalText.isVisible = nutritional != ""
        binding.nutritionalText.text = nutritional

        composition = bundle?.getString("composition") ?: ""
        binding.compositionTitle.isVisible = composition != ""
        binding.compositionText.isVisible = composition != ""
        binding.compositionText.text = composition

        termConditions = bundle?.getString("termConditions") ?: ""
        binding.termConditionsTitle.isVisible = termConditions != ""
        binding.termConditionsText.isVisible = termConditions != ""
        binding.termConditionsText.text = termConditions

        manufacturer = bundle?.getString("manufacturer") ?: ""
        binding.manufacturerTitle.isVisible = manufacturer != ""
        binding.manufacturerText.isVisible = manufacturer != ""
        binding.manufacturerText.text = manufacturer

        productWishlist = bundle?.getBoolean("productWishlist") ?: false
        productDetail = bundle?.getString("productDetail") ?: ""
        reviewList = bundle?.getSerializable("productReviews") as? ArrayList<ProductReviewModel> ?: arrayListOf()
        if (reviewList.isEmpty()){
            binding.reviewsTitle.isVisible = false
            binding.writeReview.isVisible = false
        } else {
            binding.addReviewTitle.isVisible = false
            binding.addReview.isVisible = false
        }
        val shortReviewList: ArrayList<ProductReviewModel> = arrayListOf()
        var sumMark = 0
        for (review in reviewList){
            sumMark += review.rating ?: 0
            if (shortReviewList.size < 3){
                shortReviewList.add(review)
            }
        }
        val moreThan3 = reviewList.size > 3
        val roundedRating = if (reviewList.isNotEmpty()){
            (sumMark.toDouble()/reviewList.size).toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()
        }else{ 0 }

        if (reviewList.isNotEmpty()){
            val ratingText = (sumMark.toDouble()/reviewList.size).toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
            binding.ratingProductText.text = "$ratingText"
        }

        Log.d("--A", roundedRating.toString())
        when(roundedRating){
            1->{
                binding.star1.setColorFilter(this.resources.getColor(R.color.yellow))
            }
            2->{
                binding.star1.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star2.setColorFilter(this.resources.getColor(R.color.yellow))
            }
            3->{
                binding.star1.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star2.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star3.setColorFilter(this.resources.getColor(R.color.yellow))
            }
            4->{
                binding.star1.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star2.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star3.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star4.setColorFilter(this.resources.getColor(R.color.yellow))
            }
            5->{
                binding.star1.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star2.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star3.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star4.setColorFilter(this.resources.getColor(R.color.yellow))
                binding.star5.setColorFilter(this.resources.getColor(R.color.yellow))
            }
        }
        val adapter = Product3ReviewsAdapter(this, shortReviewList, moreThan3, this)
        binding.productReviewRV.setHasFixedSize(true)
        binding.productReviewRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.productReviewRV.adapter = adapter

        product = MyBasketsProductModel(productId, productNameIn, productImageIn, productPriceIn)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productList = ProductLikeManagerService().productLike(productId.toInt(), userId)
                withContext(Dispatchers.Main){
                    val productLikeAdapter = ProductLikeAdapter(this@ShopProductActivity)
                    if (productList.isNotEmpty()) {
                        productLikeAdapter.productList = productList
                        binding.productLike.setHasFixedSize(true)
                        binding.productLike.layoutManager = LinearLayoutManager(this@ShopProductActivity, LinearLayoutManager.HORIZONTAL, false)
                        binding.productLike.adapter = productLikeAdapter
                        AnimateViewHeight().hideView(binding.loadingProductLike)
                        showViewWithAnimation(binding.productLike)
                    } else {
                        AnimateViewHeight().hideView(binding.productLikeLinear)
                    }
                }
            }catch (e:Exception){
                Log.d("--E Error", e.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun settingView() {
        binding.apply {

            //---------------------------------- AddReview -----------------------------------

            val yourName: TextInputEditText = addReviewBottomSheetView.findViewById(R.id.yourName)
            val dignityText: TextInputEditText = addReviewBottomSheetView.findViewById(R.id.dignityText)
            val flawText: TextInputEditText = addReviewBottomSheetView.findViewById(R.id.flawText)
            val commentToProduct: TextInputEditText = addReviewBottomSheetView.findViewById(R.id.commentToProduct)
            val grade1: ImageView = addReviewBottomSheetView.findViewById(R.id.grade1)
            val grade2: ImageView = addReviewBottomSheetView.findViewById(R.id.grade2)
            val grade3: ImageView = addReviewBottomSheetView.findViewById(R.id.grade3)
            val grade4: ImageView = addReviewBottomSheetView.findViewById(R.id.grade4)
            val grade5: ImageView = addReviewBottomSheetView.findViewById(R.id.grade5)
            val sendProductReview: CardView = addReviewBottomSheetView.findViewById(R.id.sendProductReview)

            var rating = 0

            grade1.setOnClickListener {
                rating = 1
                grade1.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade2.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
                grade3.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
                grade4.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
                grade5.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
            }

            grade2.setOnClickListener {
                rating = 2
                grade1.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade2.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade3.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
                grade4.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
                grade5.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
            }

            grade3.setOnClickListener {
                rating = 3
                grade1.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade2.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade3.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade4.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
                grade5.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
            }

            grade4.setOnClickListener {
                rating = 4
                grade1.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade2.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade3.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade4.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade5.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.shopGrey))
            }

            grade5.setOnClickListener {
                rating = 5
                grade1.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade2.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade3.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade4.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
                grade5.setColorFilter(this@ShopProductActivity.resources.getColor(R.color.yellow))
            }

            sendProductReview.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (yourName.text.toString() != "" && dignityText.text.toString() != "" && flawText.text.toString() != "" && commentToProduct.text.toString() != "" && rating != 0){
                            val review = AddProductReviewModel(
                                productId.toInt(),
                                rating,
                                userId,
                                dignityText.text.toString(),
                                flawText.text.toString(),
                                commentToProduct.text.toString()
                            )
                            AddProductReviewManagerService().addProductReview(review)
                            addReviewBottomSheetDialog.dismiss()
                        }
                        else {
                            withContext(Dispatchers.Main){
                                MaterialAlertDialogBuilder(this@ShopProductActivity)
                                    .setTitle("Заполните все поля и поставьте оценку!")
                                    .setMessage("Перед отправкой заполните все поля и поставьте оценку")
                                    .setPositiveButton("Понятно"){_,_ ->}
                                    .show()
                            }
                        }
                    }catch (_:Exception){
                        addReviewBottomSheetDialog.dismiss()
                    }
                }
            }

            addReview.setOnClickListener {
                val ptoken = PaykarIdStorage(this@ShopProductActivity).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(this@ShopProductActivity)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы написать отзыв товару необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(this@ShopProductActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    addReviewBottomSheetDialog.show()
                }
            }
            writeReview.setOnClickListener {
                val ptoken = PaykarIdStorage(this@ShopProductActivity).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(this@ShopProductActivity)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы написать отзыв товару необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(this@ShopProductActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    addReviewBottomSheetDialog.show()
                }
            }

            //---------------------------------- BottomSheet -----------------------------------
            val bottomSheetDialog = BottomSheetDialog(
                this@ShopProductActivity, R.style.BottomSheetDialogTheme
            )
            val bottomSheetView = LayoutInflater.from(this@ShopProductActivity).inflate(
                R.layout.layout_my_baskets_list_bottomsheet,
                findViewById(R.id.myBasketsListBottomSheet)
            )

            val myBasketsListRecycler = bottomSheetView.findViewById<RecyclerView>(R.id.myBasketsListRecycler)

            //--------------------------------------------------------------------------------

            val adapter = MyBasketsListAdapter(this@ShopProductActivity, product)

            val name = Html.fromHtml(productNameIn, Html.FROM_HTML_MODE_LEGACY).toString()
            productTitle.text = name
            productPrice.text = "$productPriceIn сомони"
            CoroutineScope(Dispatchers.Main).launch {
                Glide.with(this@ShopProductActivity)
                    .load("https://paykar.shop$productImageIn")
                    .fitCenter()
                    .placeholder(R.drawable.nophoto)
                    .into(productImage)
            }
            if (productQuan != 0.0) {
                val quanInt = if (hasDecimalPart(productQuan)){
                    productQuan
                }
                else{
                    productQuan.toInt()
                }
                addBasket.text = "В корзине $quanInt $productUnit"
            }
            productDescription.isVisible = productDetail != ""
            productDescription.text = productDetail

            addWishlist.setColorFilter(resources.getColor(R.color.black_to_white))
            addWishlist.background.setTint(resources.getColor(R.color.greyOpacity))

            addMyBaskets.setColorFilter(resources.getColor(R.color.black_to_white))
            addMyBaskets.background.setTint(resources.getColor(R.color.greyOpacity))

            if (productWishlist) {
                addWishlist.setColorFilter(resources.getColor(R.color.white))
                addWishlist.background.setTint(resources.getColor(R.color.statusBarBackground))
            }

            //Setting Background to AddList Button
            if (myBasketList.isNotEmpty()){
                for (i in myBasketList){
                    if (i.products!!.isNotEmpty()){
                        for (p in i.products){
                            if (productId == p!!.id){
                                productInMyBaskets = true
                                arrayOfIndex.add(myBasketList.indexOf(i))
                                addMyBaskets.setColorFilter(resources.getColor(R.color.white))
                                addMyBaskets.background.setTint(resources.getColor(R.color.statusBarBackground))
                            }
                        }
                    }
                }
            }

            addWishlist.setOnClickListener {
                addBlinkEffect(it)
                val ptoken = PaykarIdStorage(this@ShopProductActivity).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(this@ShopProductActivity)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы добавить товар в избранную необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(this@ShopProductActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    basketHaveProductName = ""
                    if (myBasketList.isNotEmpty()){
                        for (i in myBasketList){
                            if (i.products!!.isNotEmpty()){
                                for (p in i.products){
                                    if (productId == p!!.id){
                                        productInMyBaskets = true
                                        basketHaveProductName += "${i.name}, "
                                    }
                                }
                            }
                        }
                    }

                    if (productWishlist){
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                WishlistManagerService().deleteWishlist(userId, productId.toInt())
                                withContext(Dispatchers.Main) {
                                    addWishlist.setColorFilter(resources.getColor(R.color.black_to_white))
                                    addWishlist.background.setTint(resources.getColor(R.color.greyOpacity))
                                    productWishlist = false
                                    val snack = Snackbar.make(root, "Удален из избранных", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(this@ShopProductActivity, R.color.white))
                                    //snack.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                                    //snack.setAction("Посмотреть избранные") { }
                                    snack.show()
                                }
                            } catch (e: Exception) {
                                val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                snack.setBackgroundTint(ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground))
                                snack.setTextColor(ContextCompat.getColor(this@ShopProductActivity, R.color.white))
                                snack.show()
                            }
                        }
                    } else {
                        if (productInMyBaskets){
                            MaterialAlertDialogBuilder(this@ShopProductActivity)
                                .setTitle("Этот товар уже находится в списке корзин ${basketHaveProductName.removeRange(basketHaveProductName.length-2 until basketHaveProductName.length)}")
                                .setMessage("Хотите добавить этот товар в Избранное")
                                .setPositiveButton("Да"){_,_ ->

                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            for (i in myBasketList){
                                                if (i.products!!.isNotEmpty()){
                                                    for (p in i.products){
                                                        if (productId == p!!.id){
                                                            MyBasketStorage(this@ShopProductActivity).removeProductFromBasket(myBasketList.indexOf(i), i.products.indexOf(p))
                                                        }
                                                    }
                                                }
                                            }

                                            if (productQuan != 0.0) {
                                                getBasketService().deleteBasketItem(userId, productId.toInt())
                                            }
                                            WishlistManagerService().addToWishlist(userId, productId.toInt(), 1)
                                            withContext(Dispatchers.Main) {
                                                addWishlist.setColorFilter(resources.getColor(R.color.white))
                                                addWishlist.background.setTint(resources.getColor(R.color.statusBarBackground))

                                                addMyBaskets.setColorFilter(resources.getColor(R.color.black_to_white))
                                                addMyBaskets.background.setTint(resources.getColor(R.color.greyOpacity))

                                                //Adapter BottomSheet
                                                arrayOfIndex = arrayListOf()
                                                adapter.indexArray = arrayOfIndex
                                                adapter.nDSCH()
                                                myBasketsListRecycler.adapter = adapter

                                                productWishlist = true
                                                productQuan = 0.0
                                                addBasket.text = "В корзину"
                                                val snack = Snackbar.make(root, "Добавлен в избранное", Snackbar.LENGTH_LONG)
                                                snack.setBackgroundTint(
                                                    ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground)
                                                )
                                                snack.setTextColor(
                                                    ContextCompat.getColor(this@ShopProductActivity, R.color.white)
                                                )
                                                snack.setActionTextColor(
                                                    ContextCompat.getColor(this@ShopProductActivity, R.color.white)
                                                )
                                                snack.setAction("Посмотреть избранные") {
                                                    val intent = Intent(this@ShopProductActivity, WishlistActivity::class.java)
                                                    startActivity(intent)
                                                }
                                                snack.show()
                                            }
                                        } catch (e: Exception) {
                                            val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                            snack.setBackgroundTint(ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground))
                                            snack.setTextColor(ContextCompat.getColor(this@ShopProductActivity, R.color.white))
                                            snack.show()
                                        }
                                    }

                                }
                                .setNegativeButton("Нет"){_,_ ->}
                                .show()
                        }else{
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    if (productQuan != 0.0) {
                                        getBasketService().deleteBasketItem(userId, productId.toInt())
                                    }
                                    WishlistManagerService().addToWishlist(userId, productId.toInt(), 1)
                                    withContext(Dispatchers.Main) {
                                        addWishlist.setColorFilter(resources.getColor(R.color.white))
                                        addWishlist.background.setTint(resources.getColor(R.color.statusBarBackground))
                                        productWishlist = true
                                        productQuan = 0.0
                                        addBasket.text = "В корзину"
                                        val snack = Snackbar.make(root, "Добавлен в избранное", Snackbar.LENGTH_LONG)
                                        snack.setBackgroundTint(
                                            ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground)
                                        )
                                        snack.setTextColor(
                                            ContextCompat.getColor(this@ShopProductActivity, R.color.white)
                                        )
                                        snack.setActionTextColor(
                                            ContextCompat.getColor(this@ShopProductActivity, R.color.white)
                                        )
                                        snack.setAction("Посмотреть избранные") {
                                            val intent = Intent(this@ShopProductActivity, WishlistActivity::class.java)
                                            startActivity(intent)
                                        }
                                        snack.show()
                                    }
                                } catch (e: Exception) {
                                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                    snack.setBackgroundTint(ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground))
                                    snack.setTextColor(ContextCompat.getColor(this@ShopProductActivity, R.color.white))
                                    snack.show()
                                }
                            }
                        }
                    }
                }
            }

            addBasket.setOnClickListener {
                val ptoken = PaykarIdStorage(this@ShopProductActivity).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(this@ShopProductActivity)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы добавить товар в избранную необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(this@ShopProductActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            if (productWishlist) {
                                productWishlist = false
                                addWishlist.setColorFilter(resources.getColor(R.color.black_to_white))
                                addWishlist.background.setTint(resources.getColor(R.color.greyOpacity))
                                WishlistManagerService().deleteWishlist(userId, productId.toInt())
                            }
                            getBasketService().addBasketItem(userId, productId.toInt(), 1.0)
                            val basketCount = getBasketService().basketCount(userId)
                            UserStorageData(this@ShopProductActivity).saveBasketCount(basketCount.count)
                            withContext(Dispatchers.Main) {

                                if (addBasket.text != "В корзину") {
                                    val quanArray = addBasket.text.split(" ")
                                    val quan = if (hasDecimalPart(quanArray[2].toDouble())){
                                        quanArray[2].toDouble() + 1
                                    }
                                    else{
                                        quanArray[2].toInt() + 1
                                    }
                                    addBasket.text = "В корзине $quan $productUnit"
                                    productQuan += 1
                                } else {
                                    addBasket.text = "В корзине 1 $productUnit"
                                    productQuan = 1.0
                                }


                                val snack =
                                    Snackbar.make(root, "Добавлен в корзину", Snackbar.LENGTH_LONG)
                                snack.setBackgroundTint(
                                    ContextCompat.getColor(
                                        this@ShopProductActivity,
                                        R.color.statusBarBackground
                                    )
                                )
                                snack.setTextColor(
                                    ContextCompat.getColor(
                                        this@ShopProductActivity,
                                        R.color.white
                                    )
                                )
                                snack.setActionTextColor(
                                    ContextCompat.getColor(
                                        this@ShopProductActivity,
                                        R.color.white
                                    )
                                )
                                snack.setAction("Посмотреть корзину") {

                                    val intent = Intent(
                                        this@ShopProductActivity,
                                        BasketActivity::class.java
                                    )
                                    startActivity(intent)

                                }
                                snack.show()

                            }
                        } catch (e: Exception) {
                            val snack = Snackbar.make(
                                binding.root,
                                "Не удаётся установить соединение! Попробуйте позже",
                                Snackbar.LENGTH_LONG
                            )
                            snack.setBackgroundTint(
                                ContextCompat.getColor(
                                    this@ShopProductActivity,
                                    R.color.statusBarBackground
                                )
                            )
                            snack.setTextColor(
                                ContextCompat.getColor(
                                    this@ShopProductActivity,
                                    R.color.white
                                )
                            )
                            snack.show()
                        }
                    }
                }
            }


            //------------------------ Start working with BottomSheet ------------------------
            adapter.myBasketsList = myBasketList
            adapter.indexArray = arrayOfIndex

            val createBasket = bottomSheetView.findViewById<MaterialButton>(R.id.createBasket)
            val haveNotBasketsText = bottomSheetView.findViewById<TextView>(R.id.have_not_baskets)
            myBasketsListRecycler.setHasFixedSize(true)
            myBasketsListRecycler.layoutManager = LinearLayoutManager(this@ShopProductActivity, LinearLayoutManager.VERTICAL, false)
            myBasketsListRecycler.adapter = adapter

            createBasket.setOnClickListener {
               startActivity(Intent(this@ShopProductActivity, MyBasketsActivity::class.java))
            }

            addMyBaskets.setOnClickListener {
                val ptoken = PaykarIdStorage(this@ShopProductActivity).userToken ?: ""
                if (ptoken == "") {
                    MaterialAlertDialogBuilder(this@ShopProductActivity)
                        .setTitle("Авторизация")
                        .setMessage("Чтобы посмотреть список корзин необходимо пройти авторизацию")
                        .setPositiveButton("Авторизоваться") {_, _ ->
                            val intent = Intent(this@ShopProductActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("Отмена") {_, _ ->}
                        .show()
                } else {
                    bottomSheetDialog.setContentView(bottomSheetView)
                    bottomSheetDialog.show()
                    if (myBasketList.isEmpty()){
                        myBasketsListRecycler.isVisible = false
                        haveNotBasketsText.isVisible = true
                        createBasket.isVisible = true
                    }else{
                        myBasketsListRecycler.isVisible = true
                        haveNotBasketsText.isVisible = false
                        createBasket.isVisible = false
                    }
                }
            }

            bottomSheetDialog.setOnDismissListener {
                myBasketList = MyBasketStorage(this@ShopProductActivity).getBasketLists()
                if (myBasketList.isNotEmpty()){
                    for (i in myBasketList){
                        if (i.products!!.isNotEmpty()){
                            for (p in i.products){
                                if (productId == p!!.id){
                                    productInMyBaskets = true
                                    addMyBaskets.setColorFilter(resources.getColor(R.color.white))
                                    addMyBaskets.background.setTint(resources.getColor(R.color.statusBarBackground))

                                    if (productWishlist){
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                productWishlist = false
                                                addWishlist.setColorFilter(resources.getColor(R.color.black_to_white))
                                                addWishlist.background.setTint(resources.getColor(R.color.greyOpacity))
                                                WishlistManagerService().deleteWishlist(userId, productId.toInt())
                                            }catch (_:Exception){
                                                withContext(Dispatchers.Main) {
                                                    val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                                                    snack.setBackgroundTint(ContextCompat.getColor(this@ShopProductActivity, R.color.statusBarBackground))
                                                    snack.setTextColor(ContextCompat.getColor(this@ShopProductActivity, R.color.white))
                                                    snack.show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun seeMore() {
        val reviewsRecycler: RecyclerView = bottomSheetView.findViewById(R.id.reviewsRecycler)
        val adapter = ProductReviewsAdapter(this)
        adapter.reviews = reviewList
        reviewsRecycler.setHasFixedSize(true)
        reviewsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reviewsRecycler.adapter = adapter
        bottomSheetDialog.show()
    }

    private fun hasDecimalPart(number: Double): Boolean {
        return number % 1 != 0.0
    }

}