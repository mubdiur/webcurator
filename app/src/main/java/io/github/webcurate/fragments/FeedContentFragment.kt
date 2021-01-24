package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurate.R
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
            TODO()
        }

        fun editFeed(fragmentManager: FragmentManager) {
            CustomTitle.setTitle("Edit Feed")
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
                    when(DataProcessor.currentFeed!!.notification) {
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
        OptionMenu.feedItemVisible = true
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
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.contentEvents.value = NetEvents.DEFAULT
                    feedContentAdapter.notifyDataSetChanged()
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
            if (it == NetEvents.CURATE_CONTENTS) {
                println("curate contents")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.contentEvents.value = NetEvents.DEFAULT
                }

                CoroutineScope(Dispatchers.IO).launch {
                    Repository.curateContentsFeed(DataProcessor.currentFeed!!.id)
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
            if(it==NetEvents.NOTIFICATION_READY) {
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


        if (DataProcessor.currentFeed!!.updates != 0) {
            CoroutineScope(Dispatchers.IO).launch {
                Repository.markFeedRead(DataProcessor.currentFeed!!.id)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            NetEvents.contentEvents.value = NetEvents.CURATE_CONTENTS
        }

    } // on view

    override fun onDestroyView() {
        super.onDestroyView()
        // set option menu context to feed going back to feed
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.feed_content_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemText.text = Repository.contentList.toList()[position].text
        val sourceText =
            "Source: " + Repository.contentList.toList()[position].source.take(20) + "..."
        holder.sourceText.text = sourceText
    }

    override fun getItemCount() = Repository.contentList.size
}