package io.github.webcurate.activities.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.webcurate.R
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.databases.AuthManager
import io.github.webcurate.databinding.ActivityVerifyEmailBinding
import kotlinx.coroutines.*

class VerifyEmailActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if(AuthManager.authInstance.currentUser!=null) {
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
            AuthManager.authInstance.currentUser?.updateEmail(binding.emailVerifyEdit.text.toString())
            AuthManager.authInstance.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        println("verification email was successfully sent")
                    }
                }
            binding.emailVerifyEdit.isEnabled = false
        }

        CoroutineScope(Dispatchers.IO).launch {
            var totalDelay = 0L
            var keepChecking = true
            while (keepChecking) {
                totalDelay += 2000L
                if(AuthManager.authInstance.currentUser!=null) {
                    if(AuthManager.authInstance.currentUser?.isEmailVerified==true) {
                        withContext(Dispatchers.Main) {
                            gotoMain()
                            keepChecking = false
                        }
                    }
                        else
                    AuthManager.authInstance.currentUser?.reload()

                }
                delay(2000)
            }
        }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}