package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurate.R
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.FragmentFeedContentBinding
import io.github.webcurate.interfaces.OnPageFinish
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.networking.models.ContentResponse
import io.github.webcurate.options.OptionMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FeedContentFragment : Fragment(R.layout.fragment_feed_content), OnPageFinish {


    companion object {
        fun addSite(fragmentManager: FragmentManager) {
            TODO()
        }

        fun editFeed(fragmentManager: FragmentManager) {
            TODO()
        }

        fun deleteFeed() {
            TODO()
        }

        fun toggleNotification() {
            TODO()
        }
    }


    private var _binding: FragmentFeedContentBinding? = null
    private var stateFinish = true


    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        _binding = FragmentFeedContentBinding.bind(view)
        // set option menu context to feed item coming from feed
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED_ITEM
        OptionMenu.feedItemVisible = true
        _binding!!.contentList.layoutManager = LinearLayoutManager(requireContext())

        val feedcontentAdapter = FeedContentAdapter()
        _binding!!.contentList.adapter = feedcontentAdapter

        val contentObserver = Observer<Int> {
            if (it == NetEvents.CONTENTS_READY) {
                feedcontentAdapter.updateItems(Repository.contentList)
                if(Repository.contentList.isEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        Repository.curateContentsFeed(DataProcessor.currentFeedId)
                    }
                }
            }

            if (it==NetEvents.UPDATE_CONTENTS) {
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getContentsForFeed(DataProcessor.currentFeedId)
                }
            }
        }
        NetEvents.contentEvents.observe(requireActivity(), contentObserver)

        CoroutineScope(Dispatchers.IO).launch {
            Repository.getContentsForFeed(DataProcessor.currentFeedId)
        }

    } // on view


    override fun onDestroyView() {
        super.onDestroyView()
        // set option menu context to feed going back to feed
        _binding = null
    }

    override fun onPageFinished() {
        stateFinish = true
    }


}

class FeedContentAdapter : RecyclerView.Adapter<FeedContentAdapter.ViewHolder>() {

    private val itemList = mutableListOf<ContentResponse>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.titleTextView)
        val sourceText: TextView = itemView.findViewById(R.id.sourceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.feed_content_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemText.text = itemList[position].text
        val sourceText = "Source: " + itemList[position].source.take(20) + "..."
        holder.sourceText.text = sourceText
    }

    override fun getItemCount() = itemList.size

    fun updateItems(contents: List<ContentResponse>) {
        itemList.clear()
        itemList.addAll(contents)
        notifyDataSetChanged()
    }
}