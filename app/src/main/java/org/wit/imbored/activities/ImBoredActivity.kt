package org.wit.imbored.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImboredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        Timber.i("ImBored Activity started...")

        if (intent.hasExtra("activity_edit")) {
            activityItem = intent.extras?.getParcelable("activity_edit")!!
            binding.activityTitle.setText(activityItem.title)
            binding.description.setText(activityItem.description)
        }

        binding.btnAdd.setOnClickListener {
            activityItem.title = binding.activityTitle.text.toString()
            activityItem.description = binding.description.text.toString()
            if (activityItem.title!!.isNotEmpty()) {
                app.activities.create(activityItem.copy())
                Timber.i("Add Button Pressed: ${activityItem}")
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar
                    .make(it, "Please Enter a title", Snackbar.LENGTH_LONG)
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
