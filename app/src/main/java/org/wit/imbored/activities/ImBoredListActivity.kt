package org.wit.imbored.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
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

    private val mapIntentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImboredListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = ImBoredAdapter(app.activities.findAll(), this)

        // Initialize the Floating Action Menu and its actions
        val fabMenu: FloatingActionMenu = findViewById(R.id.fab_menu)
        floatingActionHelper = FloatingActionHelper(this, fabMenu) { filteredActivities ->
            binding.recyclerView.adapter = ImBoredAdapter(filteredActivities, this)
        }
        floatingActionHelper.setupFAB()

        // Set up Map button
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_map -> {
                    val launcherIntent = Intent(this, ImboredMapsActivity::class.java)
                    launcherIntent.putExtra("filteredActivities", ArrayList(floatingActionHelper.getFilteredActivities()))
                    mapIntentLauncher.launch(launcherIntent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityClick(activityItem: ImBoredModel) {
        val launcherIntent = Intent(this, ImBoredActivity::class.java)
        launcherIntent.putExtra("activity_edit", activityItem)
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(0, app.activities.findAll().size)
            }
        }
}
