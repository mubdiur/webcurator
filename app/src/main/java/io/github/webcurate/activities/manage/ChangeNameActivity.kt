package io.github.webcurate.activities.manage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.webcurate.R
import io.github.webcurate.databinding.ActivityChangeNameBinding

class ChangeNameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityChangeNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}