package org.wit.imbored.main

import android.app.Application
import org.wit.imbored.models.ImBoredMemStore
import org.wit.imbored.models.ImBoredModel
import timber.log.Timber

class MainApp : Application() {

    val activities = ImBoredMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("ImBored application started")
//        activities.add(ImBoredModel("Activity One", "About activity one..."))
//        activities.add(ImBoredModel("Activity Two", "About activity two..."))
//        activities.add(ImBoredModel("Activity Three", "About activity three..."))
    }
}
