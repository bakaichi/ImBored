package org.wit.imbored.helpers

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import org.wit.imbored.R
import org.wit.imbored.activities.ImBoredActivity
import org.wit.imbored.main.MainApp
import org.wit.imbored.models.ImBoredModel

class FloatingActionHelper(
    private val context: Context,
    private val fabMenu: FloatingActionMenu,
    private val onActivitiesFiltered: (List<ImBoredModel>) -> Unit
) {
    private val app = context.applicationContext as MainApp
    private var filteredActivities: List<ImBoredModel> = app.activities.findAll()

    fun setupFAB() {
        // Add Activity FAB
        val fabAdd: FloatingActionButton = fabMenu.findViewById(R.id.item_add)
        fabAdd.setOnClickListener {
            val launcherIntent = Intent(context, ImBoredActivity::class.java)
            if (context is AppCompatActivity) {
                context.startActivity(launcherIntent)
            }
            fabMenu.close(true) // Close the FAB menu after action
        }

        // Filter Activities FAB
        val fabFilter: FloatingActionButton = fabMenu.findViewById(R.id.fab_filter_activity)
        fabFilter.setOnClickListener {
            showFilterDialog()
            fabMenu.close(true) // Close the FAB menu after action
        }

        // Clear Filters FAB
        val fabClearFilter: FloatingActionButton = fabMenu.findViewById(R.id.fab_clear_filter)
        fabClearFilter.setOnClickListener {
            // Reset to show all activities
            filteredActivities = app.activities.findAll()
            onActivitiesFiltered(filteredActivities)
            fabMenu.close(true) // Close the FAB menu after action
        }
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Category", "Date")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Filter Activities")
            .setItems(filterOptions) { _, which ->
                when (which) {
                    0 -> showCategoryFilter()
                    1 -> showDateFilter()
                }
            }
        builder.create().show()
    }

    private fun showCategoryFilter() {
        val categories = context.resources.getStringArray(R.array.activity_categories)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Category")
            .setItems(categories) { _, which ->
                val selectedCategory = categories[which]
                filteredActivities = app.activities.findAll().filter {
                    it.category == selectedCategory
                }
                onActivitiesFiltered(filteredActivities)
            }
        builder.create().show()
    }

    private fun showDateFilter() {
        val datePicker = DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            filteredActivities = app.activities.findAll().filter {
                it.dateTime?.contains(selectedDate) == true
            }
            onActivitiesFiltered(filteredActivities)
        }, 2024, 10, 1)
        datePicker.show()
    }

    fun getFilteredActivities(): List<ImBoredModel> {
        return filteredActivities
    }
}
