package org.wit.imbored.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import org.wit.imbored.R
import org.wit.imbored.databinding.ActivityImboredBinding
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel
import timber.log.Timber

class ImBoredActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImboredBinding
    var activityItem = ImBoredModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImboredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        Timber.i("ImBored Activity started...")

        // Set up spinner
        val categories = resources.getStringArray(R.array.activity_categories)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter

        // Check if editing an activity
        if (intent.hasExtra("activity_edit")) {
            activityItem = intent.extras?.getParcelable("activity_edit")!!
            binding.activityTitle.setText(activityItem.title)
            binding.description.setText(activityItem.description)

            // Set the spinner to the correct category value
            val categoryIndex = categories.indexOf(activityItem.category)
            if (categoryIndex >= 0) {
                binding.categorySpinner.setSelection(categoryIndex)
            }
        }

        binding.btnAdd.setOnClickListener {
            activityItem.title = binding.activityTitle.text.toString()
            activityItem.description = binding.description.text.toString()
            activityItem.category = binding.categorySpinner.selectedItem?.toString()

            if (activityItem.title!!.isNotEmpty() && activityItem.category != "Select Category") {
                app.activities.create(activityItem.copy())
                Timber.i("Add Button Pressed: ${activityItem}")
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar
                    .make(it, "Please enter a title and select a valid category", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_imbored, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
