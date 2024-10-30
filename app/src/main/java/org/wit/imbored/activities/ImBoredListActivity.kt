package org.wit.imbored.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    }

    // Check authentication status in onStart()
    override fun onStart() {
        super.onStart()
        if (app.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Refresh recycler view
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.recyclerView.adapter = ImBoredAdapter(floatingActionHelper.getFilteredActivities(), this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_map -> {
                val launcherIntent = Intent(this, ImboredMapsActivity::class.java)
                launcherIntent.putExtra("filteredActivities", ArrayList(floatingActionHelper.getFilteredActivities()))
                startActivity(launcherIntent)
                true
            }
            R.id.item_logout -> {
                app.auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityClick(activityItem: ImBoredModel) {
        val launcherIntent = Intent(this, ImBoredActivity::class.java)
        launcherIntent.putExtra("activity_edit", activityItem)
        startActivity(launcherIntent)
    }
}
