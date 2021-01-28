package io.github.webcurate.activities.manage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.webcurate.R
import io.github.webcurate.data.AuthManager
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.ActivityChangeNameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeNameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityChangeNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(this.window.attributes.token, 0)
            finish()
        }

        val dialogBuilder = AlertDialog.Builder(this)

        binding.saveButton.setOnClickListener {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(this.window.attributes.token, 0)
            if (binding.passwordInput.text.isNullOrEmpty()) {
                binding.passwordInput.error = "This field cannot be blank!"
                binding.passwordInput.requestFocus()
            }
            if (binding.nameInput.text.isNullOrEmpty()) {
                binding.nameInput.error = "This field cannot be blank"
                binding.nameInput.requestFocus()
            }
            if (binding.nameInput.text!!.isNotEmpty() && binding.passwordInput.text!!.isNotEmpty()) {
                val user = AuthManager.authInstance.currentUser!!
                val credential = EmailAuthProvider.getCredential(
                    user.email.toString(),
                    binding.passwordInput.text.toString()
                )
                binding.urlProgress.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // account creation successful
                            // Logic for verifying email
                            val request = UserProfileChangeRequest.Builder()
                            request.displayName = binding.nameInput.text.toString()
                            AuthManager.authInstance.currentUser?.updateProfile(request.build())
                                ?.addOnCompleteListener {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        NetEvents.authEvents.value = NetEvents.NAME_CHANGED
                                    }
                                    binding.urlProgress.visibility = View.INVISIBLE
                                    finish()
                                }
                        } else {
                            binding.urlProgress.visibility = View.INVISIBLE
                            dialogBuilder.setTitle("Error!")
                            dialogBuilder.setMessage(it.exception?.message.toString())
                            dialogBuilder.create().show()
                        }
                    }
            }
        }
    }
}