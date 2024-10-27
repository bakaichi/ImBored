package org.wit.imbored.models

import timber.log.Timber

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class ImBoredMemStore : ImBoredStore {

    val activities = ArrayList<ImBoredModel>()

    override fun findAll(): List<ImBoredModel> {
        return activities
    }

    override fun create(activity: ImBoredModel) {
        activity.id = getId()
        activities.add(activity)
        logAll()
    }

    override fun update(activity: ImBoredModel) {
        val foundActivity: ImBoredModel? = activities.find { p -> p.id == activity.id }
        if (foundActivity != null) {
            foundActivity.title = activity.title
            foundActivity.description = activity.description
            foundActivity.category = activity.category
            foundActivity.image = activity.image
            foundActivity.lat = activity.lat
            foundActivity.lng = activity.lng
            foundActivity.zoom = activity.zoom
            logAll()
        }
    }

    private fun logAll() {
        activities.forEach { Timber.i("$it") }
    }
}
