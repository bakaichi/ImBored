package org.wit.imbored.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.clans.fab.FloatingActionMenu
import org.wit.imbored.R
import org.wit.imbored.adapters.ImBoredAdapter
import org.wit.imbored.adapters.ImBoredListener
import org.wit.imbored.databinding.ActivityImboredListBinding
import org.wit.imbored.helpers.FloatingActionHelper
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel

class ImBoredListActivity : AppCompatActivity(), ImBoredListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityImboredListBinding
    private lateinit var floatingActionHelper: FloatingActionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImboredListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ImBoredAdapter(app.activities.findAll(), this)

        // Initialize Floating Action Menu
        val fabMenu: FloatingActionMenu = findViewById(R.id.fab_menu)
        floatingActionHelper = FloatingActionHelper(this, fabMenu) { filteredActivities ->
            binding.recyclerView.adapter = ImBoredAdapter(filteredActivities, this)
        }
        floatingActionHelper.setupFAB()

        // Set up Map button in toolbar
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_map -> {
                    val launcherIntent = Intent(this, ImboredMapsActivity::class.java)
                    launcherIntent.putExtra("filteredActivities", ArrayList(floatingActionHelper.getFilteredActivities()))
                    startActivity(launcherIntent)
                    true
                }
                else -> false
            }
        }
    }

    // Refresh recycler view
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.recyclerView.adapter = ImBoredAdapter(floatingActionHelper.getFilteredActivities(), this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityClick(activityItem: ImBoredModel) {
        val launcherIntent = Intent(this, ImBoredActivity::class.java)
        launcherIntent.putExtra("activity_edit", activityItem)
        startActivity(launcherIntent)
    }
}
