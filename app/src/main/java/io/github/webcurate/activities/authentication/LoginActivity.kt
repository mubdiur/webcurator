package io.github.webcurate.activities.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.webcurate.R
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.databases.AuthManager
import io.github.webcurate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gotoSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            var proceed = true
            if(binding.passwordEdit.text!!.isEmpty()) {
                binding.passwordEdit.error = "This field cannot be empty!"
                proceed = false
                binding.passwordEdit.requestFocus()
            }
            if(binding.editEmail.text!!.isEmpty()) {
                binding.editEmail.error = "This field cannot be empty!"
                proceed = false
                binding.editEmail.requestFocus()
            }
            if(proceed) {
                AuthManager.authInstance.signInWithEmailAndPassword(
                    binding.editEmail.text.toString(),
                    binding.passwordEdit.text.toString()
                ).addOnCompleteListener {
                    if(it.isSuccessful) {
                        //
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}