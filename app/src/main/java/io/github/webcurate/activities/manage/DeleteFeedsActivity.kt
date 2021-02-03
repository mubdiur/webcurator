package io.github.webcurate.activities.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurate.R
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.ActivityDeleteFeedsBinding
import io.github.webcurate.networking.apis.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteFeedsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebCurator)
        super.onCreate(savedInstanceState)
        val binding = ActivityDeleteFeedsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.backButton.setOnClickListener {
            finish()
        }

        //RecyclerView
        binding.feedsDeleteList.layoutManager = LinearLayoutManager(this)
        binding.feedsDeleteList.adapter = DeleteFeedsAdapter()
        // fetch feeds
        NetEvents.feedDeleteEvents.observe(this, {

            if (it == NetEvents.UPDATE_FEEDS) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedDeleteEvents.value = NetEvents.DEFAULT
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getUserFeeds()
                }
            }

            if (it == NetEvents.FEEDS_READY) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedDeleteEvents.value = NetEvents.DEFAULT
                    (binding.feedsDeleteList.adapter as DeleteFeedsAdapter).notifyDataSetChanged()
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
        NetEvents.feedDeleteEvents.removeObservers(this)
    }
}

class DeleteFeedsAdapter : RecyclerView.Adapter<DeleteFeedsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.titleTextView)
        val delButton: ImageButton = itemView.findViewById(R.id.delBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.delete_feeds_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.text = Repository.feedList.toList()[position].title
        holder.delButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Repository.deleteFeed(Repository.feedList.toList()[position].id)
            }
        }
    }

    override fun getItemCount() = Repository.feedList.size
}