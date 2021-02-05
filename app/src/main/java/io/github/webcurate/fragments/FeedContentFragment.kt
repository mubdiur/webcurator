package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurate.R
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.FragmentFeedContentBinding
import io.github.webcurate.interfaces.OnPageFinish
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.options.OptionMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FeedContentFragment : Fragment(R.layout.fragment_feed_content), OnPageFinish {


    companion object {
        fun addSite(fragmentManager: FragmentManager) {
            DataProcessor.siteAddMode = true
            fragmentManager.commit {
                replace<PageFragment>(R.id.fragmentFeedContent, tag = "pageFragment")
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        fun editFeed(fragmentManager: FragmentManager) {
            CustomTitle.setTitle("Edit Feed")
            OptionMenu.feedItemEditing = true
            fragmentManager.commit {
                replace<FeedEditFragment>(R.id.fragmentFeedContent)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        fun deleteFeed() {
            CoroutineScope(Dispatchers.IO).launch {
                Repository.deleteFeed(DataProcessor.currentFeed!!.id)
            }
        }

        fun toggleNotification() {
            CoroutineScope(Dispatchers.IO).launch {
                Repository.setNotification(
                    DataProcessor.currentFeed!!.id,
                    when (DataProcessor.currentFeed!!.notification) {
                        1 -> 0
                        else -> 1
                    }
                )
            }
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
        OptionMenu.notify = when (DataProcessor.currentFeed!!.notification) {
            1 -> true
            else -> false
        }



        _binding!!.contentList.layoutManager = LinearLayoutManager(requireContext())

        val feedContentAdapter = FeedContentAdapter()
        _binding!!.contentList.adapter = feedContentAdapter
        /**
         * Content Events
         */
        NetEvents.contentEvents.observe(requireActivity(), {
            if (it == NetEvents.CONTENTS_READY) {
                println("Contents ready")
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.markFeedRead(DataProcessor.currentFeed!!.id)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.contentEvents.value = NetEvents.DEFAULT
                    feedContentAdapter.notifyDataSetChanged()
                    if (Repository.contentList.isEmpty()) {
                        _binding!!.listCover.visibility = View.VISIBLE
                    } else {
                        _binding!!.listCover.visibility = View.GONE
                    }
                }
            }

            if (it == NetEvents.CONTENTS_INVALID) {
                println("Contents ready")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.contentEvents.value = NetEvents.DEFAULT
                }
                CustomTitle.resetTitle()
                requireActivity().supportFragmentManager
                    .popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
            }


            if (it == NetEvents.UPDATE_CONTENTS) {
                println("Update contents")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.contentEvents.value = NetEvents.DEFAULT
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getContentsForFeed(DataProcessor.currentFeed!!.id)
                }
            }

            if (it == NetEvents.CONTENTS_CURATED) {
                println("contents are curated")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.contentEvents.value = NetEvents.DEFAULT
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getContentsForFeed(DataProcessor.currentFeed!!.id)
                }
            }

        })

        /**
         * Notification events
         */
        NetEvents.notificationEvents.observe(requireActivity(), {
            if (it == NetEvents.NOTIFICATION_READY) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.notificationEvents.value = NetEvents.DEFAULT
                }
                CustomTitle.resetTitle()
                requireActivity().supportFragmentManager
                    .popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
            }
        })



        CoroutineScope(Dispatchers.Main).launch {
            NetEvents.contentEvents.value = NetEvents.UPDATE_CONTENTS
        }

        _binding!!.reloadButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Repository.curateContentsFeed(DataProcessor.currentFeed!!.id)
            }
        }


    } // on create view

    override fun onDestroyView() {
        super.onDestroyView()
        OptionMenu.feedItemVisible = false
        _binding = null
        NetEvents.contentEvents.removeObservers(requireActivity())
        NetEvents.notificationEvents.removeObservers(requireActivity())
        Repository.contentList.clear()
    }

    override fun onPageFinished() {
        stateFinish = true
    }
}

class FeedContentAdapter : RecyclerView.Adapter<FeedContentAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.titleTextView)
        val sourceText: TextView = itemView.findViewById(R.id.sourceText)
        val newBadge: ImageView = itemView.findViewById(R.id.newBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.feed_content_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemText.text = Repository.contentList.toList()[position].text.trim()
        var suffixOfSource = ""
        if (Repository.contentList.toList()[position].source.length > 35)
            suffixOfSource = "..."
        val sourceText =
            "Source: " + Repository.contentList.toList()[position].source.take(35) + suffixOfSource
        holder.sourceText.text = sourceText

        if (Repository.contentList.toList()[position].new == 1) {
            holder.newBadge.visibility = View.VISIBLE
        } else {
            holder.newBadge.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            DataProcessor.contentURL = Repository.contentList.toList()[position].source
            MainActivity.nullBinding!!.viewPager.currentItem = 2
            NetEvents.browserEvents.value = NetEvents.LOAD_URL
        }
    }

    override fun getItemCount() = Repository.contentList.size
}