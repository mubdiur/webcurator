package io.github.webcurate.activities.manage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.webcurate.R
import io.github.webcurate.databinding.ActivityDeleteFeedsBinding

class DeleteFeedsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityDeleteFeedsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}