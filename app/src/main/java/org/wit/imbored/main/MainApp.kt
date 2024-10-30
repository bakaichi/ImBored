package org.wit.imbored.main

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import org.wit.imbored.models.ImBoredJSONStore
import org.wit.imbored.models.ImBoredStore
import timber.log.Timber

class MainApp : Application() {

    lateinit var activities: ImBoredStore
    lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        auth = FirebaseAuth.getInstance()
        activities = ImBoredJSONStore(applicationContext)
        Timber.i("ImBored has started")
    }
}