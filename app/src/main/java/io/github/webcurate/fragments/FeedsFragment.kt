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
import io.github.webcurate.databinding.FragmentFeedsBinding
import io.github.webcurate.interfaces.OnItemClick
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.options.OptionMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        // initialization
        _binding = FragmentFeedsBinding.bind(view)


        OptionMenu.contextType = OptionMenu.CONTEXT_FEED

        // RecyclerView
        binding.feedList.layoutManager = LinearLayoutManager(requireContext())
        val feedListAdapter = FeedListAdapter(this)
        binding.feedList.adapter = feedListAdapter

        NetEvents.feedEvents.observe(requireActivity(), {
            if (it == NetEvents.UPDATE_FEEDS) {
                println("update feeds")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedEvents.value = NetEvents.DEFAULT
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getUserFeeds()
                }
            }
            if (it == NetEvents.FEEDS_READY) {
                println("Feeds ready in Feeds")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedEvents.value = NetEvents.DEFAULT
                    feedListAdapter.notifyDataSetChanged()
                    if(Repository.feedList.isEmpty()) {
                        binding.listCover.visibility = View.VISIBLE
                    } else {
                        binding.listCover.visibility = View.GONE
                    }
                }
            }
        })

        NetEvents.authEvents.observe(requireActivity(), {
            if (it == NetEvents.TOKEN_READY) {
                println("token ready")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.authEvents.value = NetEvents.DEFAULT
                    NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        NetEvents.feedEvents.removeObservers(requireActivity())
        NetEvents.authEvents.removeObservers(requireActivity())
    }

    override fun onItemClicked(position: Int, action: Int) {
        CustomTitle.setTitle(Repository.feedList.toList()[position].title)
        OptionMenu.feedItemVisible = true
        DataProcessor.currentFeed = Repository.feedList.toList()[position]
        requireActivity().supportFragmentManager.commit {
            replace<FeedContentFragment>(R.id.feedsFragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }

    }
}


class FeedListAdapter(private val callBack: OnItemClick) :
    RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedTitle: TextView = itemView.findViewById(R.id.titleTextView)
        val feedDescription: TextView = itemView.findViewById(R.id.feed_description)
        val updates: TextView = itemView.findViewById(R.id.updateView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_root_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.feedTitle.text = Repository.feedList.toList()[position].title
        holder.feedDescription.text = Repository.feedList.toList()[position].description
        if (Repository.feedList.toList()[position].updates > 0) {
            val str = "${Repository.feedList.toList()[position].updates} updates"
            holder.updates.text = str
            holder.updates.visibility = View.VISIBLE
        } else {
            holder.updates.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            callBack.onItemClicked(position)
        }
    }

    override fun getItemCount() = Repository.feedList.size
}
