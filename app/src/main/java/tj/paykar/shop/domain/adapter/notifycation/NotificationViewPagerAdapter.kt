package tj.paykar.shop.domain.adapter.notifycation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import tj.paykar.shop.presentation.notification.LoyaltyNotificationFragment
import tj.paykar.shop.presentation.notification.ShopNotificationFragment
import tj.paykar.shop.presentation.notification.WalletNotificationFragment

class NotificationViewPagerAdapter(fragmentActivity: FragmentActivity?) : FragmentStateAdapter(
    fragmentActivity!!
) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShopNotificationFragment()
            1 -> LoyaltyNotificationFragment()
            2 -> WalletNotificationFragment()
            else -> ShopNotificationFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}