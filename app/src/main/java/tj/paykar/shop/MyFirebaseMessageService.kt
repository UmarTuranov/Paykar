package tj.paykar.shop

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.presentation.notification.NotificationActivity

const val channel_id = "notification_channel"
const val channel_name = "tj.paykar.shop"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessageService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null){
            generateNotifycation(message.notification!!.title!!, message.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        UserStorageData(applicationContext).saveFirebaseToken(token)
        Log.d("---D Refreshed", "Refreshed token: $token")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun generateNotifycation(title: String, message: String) {
        Log.d("Does it work???", "YES")
        val intent = Intent(this, NotificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channel_id)
            .setSmallIcon(R.drawable.icon_logo_green)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notifycationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifycationChannel = NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_HIGH)
        notifycationManager.createNotificationChannel(notifycationChannel)

        notifycationManager.notify(0, builder.build())
    }

}