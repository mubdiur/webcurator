package io.github.webcurate.activities.manage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import io.github.webcurate.R
import io.github.webcurate.data.AuthManager
import io.github.webcurate.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.backButton.setOnClickListener {
            finish()
        }

        val dialogBuilder = AlertDialog.Builder(this)

        binding.saveButton.setOnClickListener {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(this.window.attributes.token, 0)

            val emptyView: TextInputEditText? = when {
                binding.oldPassword.text.isNullOrEmpty() -> binding.oldPassword
                binding.newPassword.text.isNullOrEmpty() -> binding.newPassword
                binding.newPasswordRetyped.text.isNullOrEmpty() -> binding.newPasswordRetyped
                else -> null
            }

            when {
                emptyView != null -> {
                    emptyView.error = "This field cannot be blank!"
                    emptyView.requestFocus()
                }
                binding.newPassword.text.toString()
                        != binding.newPasswordRetyped.text.toString() -> {
                    binding.newPasswordRetyped.error = "Passwords did not match!"
                    binding.newPasswordRetyped.requestFocus()
                }
                else -> {
                    val user = AuthManager.authInstance.currentUser!!
                    val credential = EmailAuthProvider.getCredential(
                        user.email.toString(),
                        binding.oldPassword.text.toString()
                    )
                    binding.urlProgress.visibility = View.VISIBLE
                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                user.updatePassword(binding.newPassword.text.toString())
                                    .addOnCompleteListener { it2 ->
                                        binding.urlProgress.visibility = View.INVISIBLE
                                        if (it2.isSuccessful) {
                                            dialogBuilder.setTitle("Success!")
                                            dialogBuilder.setMessage("Password changed successfully!")
                                            dialogBuilder.setPositiveButton("Ok") { _, _ -> finish()}
                                            dialogBuilder.show()
                                        } else {
                                            dialogBuilder.setTitle("Error!")
                                            dialogBuilder.setMessage(it2.exception?.message.toString())
                                            dialogBuilder.setPositiveButton("Ok") { _, _ -> }
                                            dialogBuilder.show()
                                        }
                                    }
                            } else {
                                binding.urlProgress.visibility = View.INVISIBLE
                                dialogBuilder.setTitle("Error!")
                                dialogBuilder.setMessage(it.exception?.message.toString())
                                dialogBuilder.setPositiveButton("Ok") { _, _ -> }
                                dialogBuilder.show()
                            }
                        }
                }
            }
        }
    }
}