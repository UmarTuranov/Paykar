package tj.paykar.shop.presentation.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.FragmentWalletNotificationBinding
import tj.paykar.shop.domain.adapter.wallet.NotificationAdapter
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.NotificationManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation

class WalletNotificationFragment : Fragment() {
    private lateinit var binding: FragmentWalletNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalletNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNotificationList()
    }

    private fun getNotificationList() {
        val ipAddress = IpAddressStorage(requireActivity()).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(requireActivity())
        val customerId = UserStorage(requireActivity()).customerId ?: 0
        val token = UserStorage(requireActivity()).token
        val isOnline = MainManagerService().internetConnection(requireActivity())
        if (customerId != 0) {
            if (isOnline) {
                binding.loadingAnimation.isVisible = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                        val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                        val request = NotificationManagerService().getNotificationList(customerId, deviceInfo, requestInfo)
                        withContext(Dispatchers.Main) {
                            binding.loadingAnimation.isGone = true
                            Log.d("--RequestInfo", "response: ${request.body()}")
                            val response = request.body()
                            if (response?.ResultCode == 0) {
                                if (response.Notifications.isEmpty()) {
                                    showViewWithAnimation(binding.emptyLinear)
                                    HideViewWithAnimation().goneViewWithAnimation(binding.notificationRecycler)
                                } else {
                                    val adapter = NotificationAdapter(response.Notifications)
                                    binding.notificationRecycler.adapter = adapter
                                    binding.notificationRecycler.isVisible = true
                                    showViewWithAnimation(binding.notificationRecycler)
                                    HideViewWithAnimation().goneViewWithAnimation(binding.emptyLinear)
                                }
                            } else {
                                requestResultCodeAlert(response?.ResultCode ?: 0, requireActivity(), response?.ResultDesc ?: "")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d("--E Exception", e.toString())
                            MaterialAlertDialogBuilder(requireActivity())
                                .setTitle("Произошла ошибка")
                                .setMessage("Не удалось получить список уведомлений. Попробуйте ещё раз!")
                                .setPositiveButton("Попробовать снова") {dialog, _ -> dialog.cancel()
                                    getNotificationList()
                                }
                                .show()
                        }
                    }
                }
            } else {
                showViewWithAnimation(binding.emptyLinear)
                HideViewWithAnimation().goneViewWithAnimation(binding.notificationRecycler)
            }
        } else {
            showViewWithAnimation(binding.emptyLinear)
            HideViewWithAnimation().goneViewWithAnimation(binding.notificationRecycler)
        }
    }
}