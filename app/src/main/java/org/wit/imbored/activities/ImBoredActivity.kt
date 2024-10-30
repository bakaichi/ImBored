package org.wit.imbored.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.imbored.R
import org.wit.imbored.databinding.ActivityImboredBinding
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel
import org.wit.imbored.models.Location
import timber.log.Timber

class ImBoredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImboredBinding
    private var activityItem = ImBoredModel()
    private lateinit var app: MainApp
    private var edit = false
    private lateinit var imageIntentLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImboredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        // Set up recurrence options and adapter
        val recurrenceOptions = resources.getStringArray(R.array.recurrence_options)
        val recurrenceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            recurrenceOptions
        )
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.recurrenceSpinner.adapter = recurrenceAdapter

        //  listener for recurrence selection
        binding.recurrenceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    activityItem.recurrence = recurrenceOptions[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    activityItem.recurrence = "None"
                }
            }

        // Set up spinner with categories
        val categories = resources.getStringArray(R.array.activity_categories)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter

        binding.chooseDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"
                    activityItem.dateTime =
                        "$formattedDate ${activityItem.dateTime?.split(" ")?.getOrNull(1) ?: ""}"
                    binding.chooseDate.text = formattedDate
                },
                2024,  // Default Year
                10,     // Default Month
                1      // Default Day
            )
            datePicker.show()
        }

        binding.chooseTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                    activityItem.dateTime =
                        "${activityItem.dateTime?.split(" ")?.getOrNull(0) ?: ""} $formattedTime"
                    binding.chooseTime.text = formattedTime
                },
                12,    // Default Hour
                0,     // Default Minute
                true  // 24-hour clock
            )
            timePicker.show()
        }

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

            // Set the category spinner to the correct category value when editing
            val categoryIndex = categories.indexOf(activityItem.category)
            if (categoryIndex >= 0) {
                binding.categorySpinner.setSelection(categoryIndex)
            }

            // Set the recurrence spinner to the correct value when editing
            val recurrenceIndex = recurrenceOptions.indexOf(activityItem.recurrence)
            if (recurrenceIndex >= 0) {
                binding.recurrenceSpinner.setSelection(recurrenceIndex)
            }

            // Set the date button text to the current date if it exists
            val dateTime = activityItem.dateTime?.split(" ")
            if (dateTime != null && dateTime.size > 1) {
                binding.chooseDate.text = dateTime[0] // Set the date part
                binding.chooseTime.text = dateTime[1] // Set the time part
            } else if (dateTime != null && dateTime.size == 1) {
                binding.chooseDate.text = dateTime[0] // Set the date if only date exists
            }

            // Update button text
            binding.btnAdd.setText(R.string.edit_activity)
        }

        // Set listener for choose image button
        binding.chooseImage.setOnClickListener {
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            imageIntentLauncher.launch(request)
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

            // Capture date, time, and recurrence changes
            val selectedDate = binding.chooseDate.text.toString()
            val selectedTime = binding.chooseTime.text.toString()

            // Only update dateTime if the date and time were selected (not default)
            if (selectedDate != "Select Date" && selectedTime != "Select Time") {
                activityItem.dateTime = "$selectedDate $selectedTime"
            } else if (selectedDate != "Select Date") {
                activityItem.dateTime = selectedDate
            }

            activityItem.recurrence = binding.recurrenceSpinner.selectedItem.toString()

            // Validation for empty fields
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
        imageIntentLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            try {
                if (it != null) {
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    activityItem.image = it // The returned Uri
                    Timber.i("IMG :: ${activityItem.image}")
                    Picasso.get()
                        .load(activityItem.image)
                        .into(binding.activityImage)
                    binding.chooseImage.setText(R.string.change_activity_image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_imbored, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                app.activities.delete(activityItem)
                setResult(RESULT_OK)
                finish()
            }

            R.id.item_cancel -> {
                setResult(RESULT_CANCELED)
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
                            val location =
                                result.data!!.extras?.getParcelable<Location>("location")!!
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