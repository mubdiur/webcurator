package io.github.webcurate.activities.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import io.github.webcurate.R
import io.github.webcurate.data.AuthManager
import io.github.webcurate.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val dialogBuilder = AlertDialog.Builder(this)



        binding.sendEmailBtn.setOnClickListener {
            hideKeyboard(binding)
            if (binding.editEmail.text?.isEmpty()==true) {
                binding.editEmail.error = "This field cannot be empty!"
                binding.editEmail.requestFocus()
            } else {
                AuthManager.authInstance.sendPasswordResetEmail(binding.editEmail.text.toString())
                    .addOnCompleteListener {
                        if(!it.isSuccessful) {
                            dialogBuilder.setTitle("Error!")
                            dialogBuilder.setMessage(it.exception?.message.toString())
                            dialogBuilder.create().show()
                        } else {
                            showSuccess(binding)
                        }
                    }
            }
        }

        binding.gotoLogin.setOnClickListener {
            hideKeyboard(binding)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    } // on create


    private fun showSuccess(binding: ActivityForgotPasswordBinding) {
        binding.emailView.visibility = View.INVISIBLE
        binding.editEmail.visibility = View.INVISIBLE
        binding.editEmailLayout.visibility = View.INVISIBLE
        binding.successText.visibility = View.VISIBLE
    }

    private fun hideKeyboard(binding: ActivityForgotPasswordBinding) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.editEmail.windowToken, 0)
    }
}