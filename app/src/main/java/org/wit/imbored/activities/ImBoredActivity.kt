package org.wit.imbored.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import org.wit.imbored.R
import org.wit.imbored.databinding.ActivityImboredBinding
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel
import timber.log.Timber

class ImBoredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImboredBinding
    var activityItem = ImBoredModel()
    lateinit var app: MainApp
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImboredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        // Set up spinner with categories
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
            edit = true
            activityItem = intent.extras?.getParcelable("activity_edit")!!
            binding.activityTitle.setText(activityItem.title)
            binding.description.setText(activityItem.description)

            // Set the spinner to the correct category value when editing
            val categoryIndex = categories.indexOf(activityItem.category)
            if (categoryIndex >= 0) {
                binding.categorySpinner.setSelection(categoryIndex)
            }
            // Set button to edit instead of adding
            binding.btnAdd.setText(R.string.edit_activity)
        }

        binding.btnAdd.setOnClickListener {
            // Capture the updated values from the input fields
            activityItem.title = binding.activityTitle.text.toString()
            activityItem.description = binding.description.text.toString()
            activityItem.category = binding.categorySpinner.selectedItem?.toString()

            if (activityItem.title!!.isEmpty() || activityItem.category == getString(R.string.hint_activityCategory)) {
                Snackbar.make(it, R.string.enter_valid_title_category, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    // Update the existing activity
                    app.activities.update(activityItem.copy())
                    Timber.i("Update Button Pressed: $activityItem")
                } else {
                    // Create a new activity
                    app.activities.create(activityItem.copy())
                    Timber.i("Add Button Pressed: $activityItem")
                }
                setResult(RESULT_OK)
                finish()
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
