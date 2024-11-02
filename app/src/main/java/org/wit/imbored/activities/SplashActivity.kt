package org.wit.imbored.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import org.wit.imbored.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val mainIntent = Intent(this@SplashActivity, ImBoredListActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 3000) // 3-second delay
    }
}
