package org.wit.imbored.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import org.wit.imbored.R
import org.wit.imbored.databinding.ActivityImboredMapsBinding
import org.wit.imbored.databinding.ContentImboredMapsBinding
import org.wit.imbored.helpers.FloatingActionHelper
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel

class ImboredMapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityImboredMapsBinding
    private lateinit var contentBinding: ContentImboredMapsBinding
    private lateinit var map: GoogleMap
    lateinit var app: MainApp

    private lateinit var floatingActionHelper: FloatingActionHelper
    private var filteredActivities: List<ImBoredModel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp

        binding = ActivityImboredMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        contentBinding = ContentImboredMapsBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)

        filteredActivities = app.activities.findAll()

        val fabMenu: FloatingActionMenu = findViewById(R.id.fab_menu)
        floatingActionHelper = FloatingActionHelper(this, fabMenu) { activities ->
            filteredActivities = activities
            // Update the map markers based on the filtered activities
            configureMap()
        }
        floatingActionHelper.setupFAB()

        contentBinding.mapView.getMapAsync {
            map = it
            configureMap()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Long
        val activity = app.activities.findById(tag)
        contentBinding.currentTitle.text = activity!!.title
        contentBinding.currentDescription.text = activity.description
        Picasso.get().load(activity.image).into(contentBinding.currentImage)
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }

    private fun configureMap() {
        map.clear() // Clear existing markers
        map.uiSettings.isZoomControlsEnabled = true

        if (filteredActivities.isNotEmpty()) {
            val lastActivity = filteredActivities.last()
            val initialLocation = LatLng(lastActivity.lat, lastActivity.lng)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, lastActivity.zoom))
        }

        filteredActivities.forEach {
            val loc = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(it.title).position(loc)
            map.addMarker(options)?.tag = it.id
        }
        map.setOnMarkerClickListener(this)
    }
}
