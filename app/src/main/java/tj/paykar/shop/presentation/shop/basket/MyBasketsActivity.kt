package tj.paykar.shop.presentation.shop.basket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.MyBasketModel
import tj.paykar.shop.data.storage.MyBasketStorage
import tj.paykar.shop.databinding.ActivityMyBasketsBinding
import tj.paykar.shop.domain.adapter.basket.MyBasketsAdapter

class MyBasketsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyBasketsBinding
    var adapter: MyBasketsAdapter = MyBasketsAdapter(this)
    var myBasketList: ArrayList<MyBasketModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyBasketsBinding.inflate(layoutInflater)
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
    }

    override fun onResume() {
        super.onResume()
        setupAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {
        binding.apply {
            myBasketList = MyBasketStorage(this@MyBasketsActivity).getBasketLists()
            adapter.myBasketsList = myBasketList
            myBasketsRecycler.setHasFixedSize(true)
            myBasketsRecycler.layoutManager = LinearLayoutManager(this@MyBasketsActivity, LinearLayoutManager.VERTICAL, false)
            myBasketsRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupView() {
        binding.apply {
            // BottomSheet Settings
            val bottomSheetDialog = BottomSheetDialog(
                this@MyBasketsActivity, R.style.BottomSheetDialogTheme
            )
            val bottomSheetView = LayoutInflater.from(this@MyBasketsActivity).inflate(
                R.layout.layout_bottom_sheet,
                findViewById(R.id.newBasketBottomSheet)
            )
            addNewBasket.setOnClickListener{
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            }
            val basketName = bottomSheetView.findViewById<TextInputEditText>(R.id.newBasketName)
            val basketDescription = bottomSheetView.findViewById<TextInputEditText>(R.id.newBasketDescription)
            val newBasketNameLayout = bottomSheetView.findViewById<TextInputLayout>(R.id.newBasketNameLayout)

            bottomSheetView.findViewById<View>(R.id.addNewBasketButton).setOnClickListener{
                if (basketName.text.toString() != ""){
                    MyBasketStorage(this@MyBasketsActivity).createNewBasket(basketName.text.toString(), basketDescription.text.toString(), null)
                    myBasketList = MyBasketStorage(this@MyBasketsActivity).getBasketLists()
                    adapter.myBasketsList = myBasketList
                    adapter.notifyDataSetChanged()
                    bottomSheetDialog.dismiss()
                }else{
                    newBasketNameLayout.isErrorEnabled = true
                    newBasketNameLayout.error = getString(R.string.newBasketNameLayoutError)
                }

                //MyBasketStorage(this@MyBasketsActivity).removeAllBaskets()
            }
        }
    }
}