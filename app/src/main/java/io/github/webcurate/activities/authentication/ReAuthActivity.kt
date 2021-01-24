package io.github.webcurate.activities.authentication

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import io.github.webcurate.R
import io.github.webcurate.data.AuthManager
import io.github.webcurate.databinding.ActivityReAuthBinding

class ReAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)

        val binding = ActivityReAuthBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val dialogBuilder = AlertDialog.Builder(this)

        binding.reAuthButton.setOnClickListener {
            hideKeyboard(binding)
            if (binding.passwordEdit.text?.isEmpty() == true) {
                binding.passwordEdit.error = "This field cannot be empty!"
                binding.passwordEdit.requestFocus()
            } else {
                AuthManager.authInstance.currentUser?.reauthenticate(
                    EmailAuthProvider.getCredential(
                        AuthManager.authInstance.currentUser!!.email.toString(),
                        binding.passwordEdit.text.toString()
                    )
                )?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        finish()
                    } else {
                        dialogBuilder.setTitle("Error!")
                        dialogBuilder.setMessage(it.exception?.message.toString())
                        dialogBuilder.create().show()
                    }
                }
            }
        }
    } // on create

    private fun hideKeyboard(binding: ActivityReAuthBinding) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.passwordEdit.windowToken, 0)
    }
}