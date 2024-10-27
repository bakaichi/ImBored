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
import org.wit.imbored.models.Location
import timber.log.Timber

class ImBoredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImboredBinding
    private var activityItem = ImBoredModel()
    private lateinit var app: MainApp
    private var edit = false
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

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

            // Set input fields with existing values
            binding.activityTitle.setText(activityItem.title)
            binding.description.setText(activityItem.description)
            Picasso.get().load(activityItem.image).into(binding.activityImage)

            if (activityItem.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_activity_image)
            }

            // Set the spinner to the correct category value when editing
            val categoryIndex = categories.indexOf(activityItem.category)
            if (categoryIndex >= 0) {
                binding.categorySpinner.setSelection(categoryIndex)
            }

            // Update button text
            binding.btnAdd.setText(R.string.edit_activity)
        }

        // Set listener for choose image button
        binding.chooseImage.setOnClickListener {
            Timber.i("Select image")
            showImagePicker(imageIntentLauncher)
        }

        // Set listener for activity location button
        binding.activityLocation.setOnClickListener {
            val location = Location(51.8985, -8.4756, 15f)
            if (activityItem.zoom != 0f) {
                location.lat = activityItem.lat
                location.lng = activityItem.lng
                location.zoom = activityItem.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        binding.btnAdd.setOnClickListener {
            // Capture the updated values from the input fields
            activityItem.title = binding.activityTitle.text.toString()
            activityItem.description = binding.description.text.toString()
            activityItem.category = binding.categorySpinner.selectedItem?.toString()

            if (activityItem.title!!.isEmpty() || activityItem.category == getString(R.string.hint_activityCategory)) {
                Snackbar.make(it, R.string.enter_valid_title_category, Snackbar.LENGTH_LONG).show()
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

        // Register the image picker callback
        registerImagePickerCallback()

        // Register the map callback
        registerMapCallback()
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            val uri = result.data!!.data!!
                            Timber.i("Got Result $uri")
                            activityItem.image = uri
                            // Load the image into the ImageView using Picasso
                            Picasso.get()
                                .load(activityItem.image)
                                .into(binding.activityImage)
                            // give persistent URI permission
                            contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                            binding.chooseImage.setText(R.string.change_activity_image)
                        }
                    }
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

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location ${result.data.toString()}")
                            // Retrieve updated location and set it to activityItem
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location == $location")
                            activityItem.lat = location.lat
                            activityItem.lng = location.lng
                            activityItem.zoom = location.zoom
                        }
                    }
                }
            }
    }
}
