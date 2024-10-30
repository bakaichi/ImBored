package org.wit.imbored.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.wit.imbored.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth // Firebase Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Snackbar.make(binding.root, "Please enter email and password", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to main activity
                    val user = auth.currentUser
                    startActivity(Intent(this, ImBoredListActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Snackbar.make(binding.root, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
