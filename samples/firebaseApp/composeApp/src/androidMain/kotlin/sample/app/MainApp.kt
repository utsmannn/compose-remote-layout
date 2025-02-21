package sample.app

import android.app.Application
import com.google.firebase.FirebaseApp

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
