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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurate.R
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.FragmentFeedsBinding
import io.github.webcurate.interfaces.OnItemClick
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.networking.models.FeedResponse
import io.github.webcurate.options.OptionMenu
import kotlinx.coroutines.*


class FeedsFragment : Fragment(R.layout.fragment_feeds), OnItemClick {

    companion object {
        fun addFeed(fragmentManager: FragmentManager) {
            CustomTitle.setTitle("Create Feed - Basic Info")
            fragmentManager.commit {
                replace<FeedNameFragment>(R.id.feedsFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }

    private var _binding: FragmentFeedsBinding? = null


    private val binding get() = _binding!!
    private var feedList = listOf<FeedResponse>()


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        // initialization
        _binding = FragmentFeedsBinding.bind(view)


        OptionMenu.contextType = OptionMenu.CONTEXT_FEED

        // RecyclerView
        binding.feedList.layoutManager = LinearLayoutManager(requireContext())
        binding.feedList.adapter = FeedListAdapter(listOf(), this)


        val feedEventObserver = Observer<Int> {
            if (it == NetEvents.UPDATE_FEEDS) {
                NetEvents.feedEvents.value = NetEvents.DEFAULT
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getUserFeeds()
                }
            }
            if (it == NetEvents.FEEDS_READY) {
                NetEvents.feedEvents.value = NetEvents.DEFAULT
                updateUI(Repository.feedList)
            }
        } // feed event observer
        val authEventObserver = Observer<Int> {
            if (it == NetEvents.TOKEN_READY) {
                NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)
            }
        } // feed event observer


        NetEvents.feedEvents.observe(requireActivity(), feedEventObserver)
        NetEvents.authEvents.observe(requireActivity(), authEventObserver)

    }

    private fun updateUI(feeds: List<FeedResponse>) {
        feedList = feeds
        println("${feeds.size}")

        binding.feedList.adapter = FeedListAdapter(feedList, this@FeedsFragment)
        binding.feedList.adapter?.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(position: Int) {
        MainActivity.nullBinding?.titleText?.text = feedList[position].title
        CustomTitle.setTitle(feedList[position].title)
        OptionMenu.feedItemVisible = true
        DataProcessor.currentFeedId = feedList[position].id
        requireActivity().supportFragmentManager.commit {
            replace<FeedContentFragment>(R.id.feedsFragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }

    }
}


class FeedListAdapter(private val feedList: List<FeedResponse>, private val callBack: OnItemClick) :
    RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedTitle: TextView = itemView.findViewById(R.id.content_text)
        val feedDescription: TextView = itemView.findViewById(R.id.feed_description)
        val updates: TextView = itemView.findViewById(R.id.updateView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_root_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.feedTitle.text = feedList[position].title
        holder.feedDescription.text = feedList[position].description
        if (feedList[position].updates > 0) {
            val str = "${feedList[position].updates} updates"
            holder.updates.text = str
            holder.updates.visibility = View.VISIBLE
        } else {
            holder.updates.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            callBack.onItemClicked(position)
        }
    }

    override fun getItemCount() = feedList.size
}
