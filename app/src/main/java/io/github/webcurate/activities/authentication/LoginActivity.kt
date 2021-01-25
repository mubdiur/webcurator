package io.github.webcurate.activities.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.github.webcurate.R
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.data.AuthManager
import io.github.webcurate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gotoSignUp.setOnClickListener {
            hideKeyboard(binding)
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        val dialogBuilder = AlertDialog.Builder(this)


        binding.loginButton.setOnClickListener {
            hideKeyboard(binding)
            var proceed = true
            if (binding.passwordEdit.text!!.isEmpty()) {
                binding.passwordEdit.error = "This field cannot be empty!"
                proceed = false
                binding.passwordEdit.requestFocus()
            }
            if (binding.editEmail.text!!.isEmpty()) {
                binding.editEmail.error = "This field cannot be empty!"
                proceed = false
                binding.editEmail.requestFocus()
            }
            if (proceed) {
                binding.urlProgress.visibility = View.VISIBLE
                AuthManager.authInstance.signInWithEmailAndPassword(
                    binding.editEmail.text.toString(),
                    binding.passwordEdit.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.urlProgress.visibility = View.INVISIBLE
                        //
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        binding.urlProgress.visibility = View.INVISIBLE
                        dialogBuilder.setTitle("Error!")
                        dialogBuilder.setMessage(it.exception?.message.toString())
                        dialogBuilder.create().show()
                    }
                }
            }
        } // on click

        binding.passwordResetButton.setOnClickListener {
            hideKeyboard(binding)
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }
    } // on create

    private fun hideKeyboard(binding: ActivityLoginBinding) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.passwordEdit.windowToken, 0)
    }
}