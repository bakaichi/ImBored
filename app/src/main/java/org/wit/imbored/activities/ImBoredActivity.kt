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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImboredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        recurrenceSpinner()
        categorySpinner()
        dateButton()
        timeButton()

        if (intent.hasExtra("activity_edit")) {
            edit = true
            activityItem = intent.extras?.getParcelable("activity_edit")!!
            binding.activityTitle.setText(activityItem.title)
            binding.description.setText(activityItem.description)
            binding.activityLocation.setText(R.string.change_location)

            Picasso.get().load(activityItem.image).into(binding.activityImage)
            if (activityItem.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_activity_image)
            }
            val categories = resources.getStringArray(R.array.activity_categories)
            val categoryIndex = categories.indexOf(activityItem.category)
            if (categoryIndex >= 0) {
                binding.categorySpinner.setSelection(categoryIndex)
            }
            val recurrenceOptions = resources.getStringArray(R.array.recurrence_options)
            val recurrenceIndex = recurrenceOptions.indexOf(activityItem.recurrence)
            if (recurrenceIndex >= 0) {
                binding.recurrenceSpinner.setSelection(recurrenceIndex)
            }
            val dateTime = activityItem.dateTime?.split(" ")
            if (dateTime != null && dateTime.size > 1) {
                binding.chooseDate.text = dateTime[0]
                binding.chooseTime.text = dateTime[1]
            } else if (dateTime != null && dateTime.size == 1) {
                binding.chooseDate.text = dateTime[0]
            }
            binding.btnAdd.setText(R.string.edit_activity)
        }

        binding.chooseImage.setOnClickListener {
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            imageIntentLauncher.launch(request)
        }

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
            activityItem.title = binding.activityTitle.text.toString()
            activityItem.description = binding.description.text.toString()
            activityItem.category = binding.categorySpinner.selectedItem?.toString()
            activityItem.recurrence = binding.recurrenceSpinner.selectedItem?.toString()
            val selectedDate = binding.chooseDate.text.toString()
            val selectedTime = binding.chooseTime.text.toString()
            if (selectedDate != "Select Date" && selectedTime != "Select Time") {
                activityItem.dateTime = "$selectedDate $selectedTime"
            } else if (selectedDate != "Select Date") {
                activityItem.dateTime = selectedDate
            }
            if (activityItem.title!!.isEmpty() || activityItem.category == getString(R.string.hint_activityCategory)) {
                Snackbar.make(it, R.string.enter_valid_title_category, Snackbar.LENGTH_LONG).show()
            } else {
                if (edit) {
                    app.activities.update(activityItem.copy())
                    Timber.i("Update Button Pressed: $activityItem")
                } else {
                    app.activities.create(activityItem.copy())
                    Timber.i("Add Button Pressed: $activityItem")
                }
                setResult(RESULT_OK)
                finish()
            }
        }

        registerImagePickerCallback()
        registerMapCallback()
    }



    private fun recurrenceSpinner() {
        val recurrenceOptions = resources.getStringArray(R.array.recurrence_options)
        val recurrenceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            recurrenceOptions
        )
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.recurrenceSpinner.adapter = recurrenceAdapter
    }

    private fun categorySpinner() {
        val categories = resources.getStringArray(R.array.activity_categories)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter
    }

    private fun dateButton() {
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
    }

    @SuppressLint("DefaultLocale")
    private fun timeButton() {
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
                    activityItem.image = it
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
