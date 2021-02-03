package io.github.webcurate.activities.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import io.github.webcurate.R
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.ActivityFeedsNotificationBinding
import io.github.webcurate.networking.apis.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch

class FeedsNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityFeedsNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            finish()
        }
        // RecyclerView
        binding.notificationList.layoutManager = LinearLayoutManager(this)
        binding.notificationList.adapter = NotificationListAdapter()
        // fetch feeds
        NetEvents.feedNotifyEvents.observe(this, {

            if (it == NetEvents.UPDATE_FEEDS) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedNotifyEvents.value = NetEvents.DEFAULT
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getUserFeeds()
                }
            }

            if (it == NetEvents.FEEDS_READY) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedNotifyEvents.value = NetEvents.DEFAULT
                    (binding.notificationList.adapter as NotificationListAdapter)
                        .notifyDataSetChanged()
                    if(Repository.feedList.isEmpty()) {
                        binding.listCover.visibility = View.VISIBLE
                    } else {
                        binding.listCover.visibility = View.GONE
                    }
                }
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            Repository.getUserFeeds()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetEvents.feedNotifyEvents.removeObservers(this)
    }
}

class NotificationListAdapter : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.titleTextView)
        val notifySwitch: SwitchMaterial = itemView.findViewById(R.id.notifySwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.feeds_notification_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.text = Repository.feedList.toList()[position].title
        holder.notifySwitch.isChecked =
            Repository.feedList.toList()[position].notification == 1

        holder.notifySwitch.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val notificationVal = when(Repository.feedList.toList()[position].notification) {
                    1 -> 0
                    else -> 1
                }
                Repository.setNotification(
                    Repository.feedList.toList()[position].id,
                    notificationVal
                )
            }
        }
    }

    override fun getItemCount() = Repository.feedList.size

}
