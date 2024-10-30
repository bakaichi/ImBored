package org.wit.imbored.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.wit.imbored.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth // Firebase Authentication instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            val confirmPassword = binding.confirmPasswordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                createAccount(email, password)
            } else {
                Snackbar.make(binding.root, "Please enter valid details", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation success navigate to main activity
                    val user = auth.currentUser
                    startActivity(Intent(this, ImBoredListActivity::class.java))
                    finish()
                } else {
                    // error message if registration fails
                    Snackbar.make(binding.root, "Registration failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
