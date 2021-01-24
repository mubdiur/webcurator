package io.github.webcurate.data

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    var authInstance = FirebaseAuth.getInstance()
    var idToken = ""
}