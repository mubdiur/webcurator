package io.github.webcurate.activities.manage

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.webcurate.R
import io.github.webcurate.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityHelpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            finish()
        }


        binding.feedTab.setImageBitmap(
            BitmapFactory.decodeStream(
                resources.openRawResource(R.raw.feed_tab)
            )
        )
        binding.selectContents.setImageBitmap(
            BitmapFactory.decodeStream(
                resources.openRawResource(R.raw.select_contents)
            )
        )
        binding.selectFeed.setImageBitmap(
            BitmapFactory.decodeStream(
                resources.openRawResource(R.raw.select_feed)
            )
        )
        binding.selectOption.setImageBitmap(
            BitmapFactory.decodeStream(
                resources.openRawResource(R.raw.select_option_first)
            )
        )
        binding.selectUrl.setImageBitmap(
            BitmapFactory.decodeStream(
                resources.openRawResource(R.raw.select_url)
            )
        )
        binding.turnNotification.setImageBitmap(
            BitmapFactory.decodeStream(
                resources.openRawResource(R.raw.turn_on_notification)
            )
        )


    }
}