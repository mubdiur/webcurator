package io.github.webcurate.activities.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.webcurate.R
import io.github.webcurate.data.AuthManager
import io.github.webcurate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dialogBuilder = AlertDialog.Builder(this)


        binding.signUpButton.setOnClickListener {
            hideKeyboard(binding)
            // check if all the fields are filled
            var proceed = true
            if (binding.passwordEdit.text!!.toString() != binding.editConfirmPassword.text!!.toString()) {
                proceed = false
                binding.editConfirmPassword.error = "This field cannot be empty!"
                binding.editConfirmPassword.requestFocus()
            }
            if (binding.editConfirmPassword.text!!.isEmpty()) {
                proceed = false
                binding.editConfirmPassword.error = "This field cannot be empty!"
                binding.editConfirmPassword.requestFocus()
            }
            if (binding.passwordEdit.text!!.isEmpty()) {
                proceed = false
                binding.passwordEdit.error = "This field cannot be empty!"
                binding.passwordEdit.requestFocus()
            }
            if (binding.editEmail.text!!.isEmpty()) {
                proceed = false
                binding.editEmail.error = "This field cannot be empty!"
                binding.editEmail.requestFocus()
            }
            if (binding.editName.text!!.isEmpty()) {
                proceed = false
                binding.editName.error = "This field cannot be empty!"
                binding.editName.requestFocus()
            }

            if (proceed) {
                AuthManager.authInstance.createUserWithEmailAndPassword(
                    binding.editEmail.text!!.toString(),
                    binding.passwordEdit.text!!.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // account creation successful
                        // Logic for verifying email
                        val request = UserProfileChangeRequest.Builder()
                        request.displayName = binding.editName.text.toString()
                        AuthManager.authInstance.currentUser?.updateProfile(request.build())
                            ?.addOnCompleteListener {
                                startActivity(Intent(this, VerifyEmailActivity::class.java))
                                finish()
                            }
                    } else {
                        dialogBuilder.setTitle("Error!")
                        dialogBuilder.setMessage(it.exception?.message.toString())
                        dialogBuilder.create().show()
                    }
                }
            }
        } // end  of sign up button


        binding.gotoSignInButton.setOnClickListener {
            hideKeyboard(binding)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    } // on create

    private fun hideKeyboard(binding: ActivityRegisterBinding) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.editConfirmPassword.windowToken, 0)
    }
}