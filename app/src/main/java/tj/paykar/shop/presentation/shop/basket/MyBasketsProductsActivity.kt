package tj.paykar.shop.presentation.shop.basket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.data.model.shop.MyBasketsProductModel
import tj.paykar.shop.data.storage.MyBasketStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityMyBasketsProductsBinding
import tj.paykar.shop.domain.adapter.basket.MyBasketsProductsAdapter
import tj.paykar.shop.domain.usecase.shop.getBasketService
import tj.paykar.shop.presentation.shop.ShopActivity

class MyBasketsProductsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyBasketsProductsBinding
    private var index: Int = 0
    var reason = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyBasketsProductsBinding.inflate(layoutInflater)
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
    }

    override fun onBackPressed() {
        if (reason != ""){
            super.onBackPressed()
        }else{
            startActivity(Intent(this, MyBasketsActivity::class.java))
        }
    }

    private fun getPutIntent() {
        binding.apply {
            val bundle: Bundle? = intent.extras
            reason = bundle?.getString("reason") ?: ""
            basketName.text = bundle?.getString("basketName") ?: ""
            val basketD = bundle?.getString("basketDescription") ?: ""
            if (basketD != ""){
                basketDescription.text = basketD
            }else{
                basketDescription.isVisible = false
            }
            index = bundle?.getInt("index") ?: 0

            val basketsList = MyBasketStorage(this@MyBasketsProductsActivity).getBasketLists()
            val productsList = basketsList[index].products

            val adapter = MyBasketsProductsAdapter(this@MyBasketsProductsActivity, index)
            adapter.myBasketsProductsList = productsList ?: arrayListOf<MyBasketsProductModel?>()
            adapter.userId = UserStorageData(this@MyBasketsProductsActivity).getUserId()

            myBasketsProductsRecycler.setHasFixedSize(true)
            myBasketsProductsRecycler.layoutManager = LinearLayoutManager(this@MyBasketsProductsActivity, LinearLayoutManager.VERTICAL, false)
            myBasketsProductsRecycler.adapter = adapter


            if (adapter.myBasketsProductsList.isEmpty()){
                sendOrder.isVisible = false
                emptyBasketList.isVisible = true
                emptyBasketListTitle.isVisible = true
            }else{
                sendOrder.isVisible = true
                emptyBasketList.isVisible = false
                emptyBasketListTitle.isVisible = false
            }
            emptyBasketList.setOnClickListener {
                startActivity(Intent(this@MyBasketsProductsActivity, ShopActivity::class.java))
            }
            //Sending Order
            sendOrder.setOnClickListener {
                if (adapter.userId != 0) {
                    sendOrder.isVisible = false
                    sendingProcess.isVisible = true
                    CoroutineScope(Dispatchers.IO).launch {
                        for (product in adapter.myBasketsProductsList){
                            try {
                                getBasketService().addBasketItem(adapter.userId, product?.id!!.toInt(), 1.0)
                            }catch (e:Exception){
                                Log.d("Error while sending order in MyBasketsProductsActivity", e.toString())
                            }
                        }
                        withContext(Dispatchers.Main){
                            try {
                                val basketCount = getBasketService().basketCount(adapter.userId)
                                UserStorageData(this@MyBasketsProductsActivity).saveBasketCount(basketCount.count)
                            }catch (_:Exception){}
                            startActivity(Intent(this@MyBasketsProductsActivity, BasketActivity::class.java))
                        }
                    }
                }
            }

            //Deleting Basket
            deleteBasket.setOnClickListener {
                MaterialAlertDialogBuilder(this@MyBasketsProductsActivity)
                    .setTitle("Удалить корзину")
                    .setMessage("Вы уверенны что хотите удалить эту корзину?")
                    .setPositiveButton("Удалить"){_,_ ->
                        MyBasketStorage(this@MyBasketsProductsActivity).deleteThisBasket(index)
                        startActivity(Intent(this@MyBasketsProductsActivity, MyBasketsActivity::class.java))
                    }
                    .setNegativeButton("Отмена"){_,_ ->}
                    .show()
            }
        }
    }
}