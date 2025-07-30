package tj.paykar.shop.presentation.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentShopNotificationBinding
import tj.paykar.shop.domain.adapter.notifycation.NotifycationAdapter

class ShopNotificationFragment : Fragment() {
    private lateinit var binding: FragmentShopNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationList = UserStorageData(requireActivity()).getNotificationList()
        val adapter = NotifycationAdapter(requireActivity())
        adapter.notifyList = notificationList
        binding.notificationsRecycler.adapter = adapter
    }
}