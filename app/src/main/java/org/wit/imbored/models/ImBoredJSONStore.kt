package org.wit.imbored.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.imbored.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "activities.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<ImBoredModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ImBoredJSONStore(private val context: Context) : ImBoredStore {

    var activities = mutableListOf<ImBoredModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<ImBoredModel> {
        logAll()
        return activities
    }

    override fun create(activity: ImBoredModel) {
        activity.id = generateRandomId()
        activities.add(activity)
        serialize()
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
            foundActivity.dateTime = activity.dateTime
            foundActivity.recurrence = activity.recurrence
            logAll()
        }
    }

    override fun findById(id: Long): ImBoredModel? {
        val foundActivity: ImBoredModel? = activities.find { it.id == id }
        return foundActivity
    }

    override fun delete(activity: ImBoredModel) {
        activities.remove(activity)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(activities, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        activities = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        activities.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}