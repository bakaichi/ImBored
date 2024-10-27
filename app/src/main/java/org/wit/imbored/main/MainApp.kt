package org.wit.imbored.main

import android.app.Application
import org.wit.imbored.models.ImBoredJSONStore
import org.wit.imbored.models.ImBoredMemStore
import org.wit.imbored.models.ImBoredStore
import timber.log.Timber

class MainApp : Application() {

    lateinit var activities: ImBoredStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        activities = ImBoredJSONStore(applicationContext)
        Timber.i("ImBored started")
    }
}