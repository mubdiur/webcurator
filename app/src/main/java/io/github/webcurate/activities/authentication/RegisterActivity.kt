package io.github.webcurate.activities.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.webcurate.R
import io.github.webcurate.databases.AuthManager
import io.github.webcurate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
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
                        // something went wrong in creating account
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } // end  of sign up button
    }
}