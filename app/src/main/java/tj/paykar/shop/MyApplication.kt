package tj.paykar.shop

import android.app.Application
import android.content.ComponentCallbacks2
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import tj.paykar.shop.data.storage.wallet.UserStorage

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.messaging.isAutoInitEnabled = true
        registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onTrimMemory(level: Int) {
                if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                    // Приложение ушло в фон
                    UserStorage(applicationContext).savePinCodeEntered(false)
                }
            }

            override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {}

            override fun onLowMemory() {}
        })
    }
}

