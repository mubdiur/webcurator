package io.github.webcurate.activities.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.webcurate.R

class ReAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_auth)
    }
}