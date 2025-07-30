package tj.paykar.shop.presentation.notification

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.FragmentLoyaltyNotificationBinding
import tj.paykar.shop.domain.adapter.notifycation.NotifycationCardAdapter
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation

class LoyaltyNotificationFragment : Fragment() {
    private lateinit var binding: FragmentLoyaltyNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoyaltyNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = NotifycationCardAdapter(requireActivity())
        super.onViewCreated(view, savedInstanceState)
        val cardInfo = UserStorageData(requireActivity()).getInfoCard()
        if (cardInfo.authorized == "true") {
            val isOnline = MainManagerService().internetConnection(requireActivity())
            if (isOnline) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val storage = UserStorageData(requireActivity())
                        val user = storage.getInfoCard()
                        adapter.sendRequest("+${user.phone}", user.token ?: "")
                        withContext(Dispatchers.Main) {
                            HideViewWithAnimation().goneViewWithAnimation(binding.loadingAnimation)
                            if (adapter.notifyList.isNotEmpty()) {
                                binding.notificationsRecycler.adapter = adapter
                                adapter.notifyDataSetChanged()
                                showViewWithAnimation(binding.notificationsRecycler)
                            } else {
                                showViewWithAnimation(binding.emptyLinear)
                            }
                        }
                    }catch (_:Exception){
                        withContext(Dispatchers.Main) {
                            val snack = Snackbar.make(binding.root, "Не удаётся установить соединение! Попробуйте позже", Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.statusBarBackground))
                            snack.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                            snack.show()
                        }
                    }
                }
            } else {
                showViewWithAnimation(binding.emptyLinear)
            }
        } else {
            showViewWithAnimation(binding.emptyLinear)
            HideViewWithAnimation().goneViewWithAnimation(binding.loadingAnimation)
        }
    }
}