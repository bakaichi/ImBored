package org.wit.imbored.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.imbored.R
import org.wit.imbored.adapters.ImBoredAdapter
import org.wit.imbored.adapters.ImBoredListener
import org.wit.imbored.databinding.ActivityImboredListBinding
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel

class ImBoredListActivity : AppCompatActivity(), ImBoredListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityImboredListBinding
    private val mapIntentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )    { }

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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, ImBoredActivity::class.java)
                getResult.launch(launcherIntent)
            } R.id.item_map -> {
                val launcherIntent = Intent(this, ImboredMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)          }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(0, app.activities.findAll().size)
            }
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
            } else
                binding.recyclerView.adapter = ImBoredAdapter(app.activities.findAll(),this)            }
}
