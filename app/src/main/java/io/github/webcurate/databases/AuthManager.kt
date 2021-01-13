package io.github.webcurate.databases

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    var authInstance = FirebaseAuth.getInstance()
}