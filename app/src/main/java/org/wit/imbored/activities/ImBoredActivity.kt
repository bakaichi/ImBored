package org.wit.imbored.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.imbored.R
import org.wit.imbored.databinding.ActivityImboredBinding
import org.wit.imbored.helpers.showImagePicker
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel
import org.wit.imbored.models.getId
import timber.log.Timber

class ImBoredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImboredBinding
    private var activityItem = ImBoredModel()
    private lateinit var app: MainApp
    private var edit = false
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImboredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        // Register the image picker callback
        registerImagePickerCallback()

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
            Picasso.get()
                .load(activityItem.image)
                .into(binding.activityImage)
            if (activityItem.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_activity_image)
            }

            // Set the spinner to the correct category value when editing
            val categoryIndex = categories.indexOf(activityItem.category)
            if (categoryIndex >= 0) {
                binding.categorySpinner.setSelection(categoryIndex)
            }

            // Set button text to indicate editing instead of adding
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

        // Set listener for choose image button
        binding.chooseImage.setOnClickListener {
            Timber.i("Select image")
            showImagePicker(imageIntentLauncher)
        }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            activityItem.image = result.data!!.data!!
                            Picasso.get()
                                .load(activityItem.image)
                                .into(binding.activityImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
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
