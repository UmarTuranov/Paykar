package tj.paykar.shop.presentation.notification

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import tj.paykar.shop.MainActivity
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityNotificationBinding
import tj.paykar.shop.domain.adapter.notifycation.NotificationViewPagerAdapter
import tj.paykar.shop.domain.adapter.notifycation.NotifycationAdapter

class NotificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotificationBinding
    var notifyAdapter = NotifycationAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
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
        //setupAdapter()
        setupView()
    }

    private fun setupView() {
        binding.viewPager.adapter = NotificationViewPagerAdapter(this)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

//    private fun setupAdapter() {
//        binding.apply {
//            val notificationList = UserStorageData(this@NotificationActivity).getNotificationList()
//            notifyAdapter.notifyList = notificationList
//            notify.setHasFixedSize(true)
//            notify.layoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
//            notify.adapter = notifyAdapter
//        }
//    }
}