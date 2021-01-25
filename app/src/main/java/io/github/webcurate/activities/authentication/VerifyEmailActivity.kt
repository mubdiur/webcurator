package io.github.webcurate.activities.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.github.webcurate.R
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.data.AuthManager
import io.github.webcurate.databinding.ActivityVerifyEmailBinding
import kotlinx.coroutines.*

class VerifyEmailActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (AuthManager.authInstance.currentUser != null) {
            AuthManager.authInstance.currentUser?.sendEmailVerification()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailVerifyEdit.isEnabled = false

        val dialogBuilder = AlertDialog.Builder(this)

        if (AuthManager.authInstance.currentUser != null) {
            binding.emailVerifyEdit.setText(
                AuthManager.authInstance.currentUser!!.email,
                TextView.BufferType.EDITABLE
            )
        } else {
            // Goto Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.editButton.setOnClickListener {
            binding.emailVerifyEdit.isEnabled = true
        }

        binding.resendVerification.setOnClickListener {
            hideKeyboard(binding)
            if(binding.emailVerifyEdit.text!!.isEmpty()) {
                binding.emailVerifyEdit.error = "This field cannot be empty"
                binding.emailVerifyEdit.requestFocus()
            } else {
                AuthManager.authInstance.currentUser?.updateEmail(binding.emailVerifyEdit.text.toString())
                AuthManager.authInstance.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener {
                        if (!it.isSuccessful) {
                            binding.urlProgress.visibility = View.INVISIBLE
                            dialogBuilder.setTitle("Error!")
                            dialogBuilder.setMessage(it.exception?.message.toString())
                            dialogBuilder.create().show()
                        } else {
                            binding.urlProgress.visibility = View.INVISIBLE
                        }
                    }

                binding.emailVerifyEdit.isEnabled = false
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            var totalDelay = 0L
            var keepChecking = true
            while (keepChecking) {
                totalDelay += 2000L
                if (AuthManager.authInstance.currentUser != null) {
                    if (AuthManager.authInstance.currentUser?.isEmailVerified == true) {
                        withContext(Dispatchers.Main) {
                            gotoMain()
                            keepChecking = false
                        }
                    } else
                        AuthManager.authInstance.currentUser?.reload()

                }
                delay(2000)
            }
        }

        binding.logoutBtn.setOnClickListener {
            hideKeyboard(binding)
            if (AuthManager.authInstance.currentUser != null) {
                AuthManager.authInstance.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    } // on create

    private fun hideKeyboard(binding: ActivityVerifyEmailBinding) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.emailVerifyEdit.windowToken, 0)
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}