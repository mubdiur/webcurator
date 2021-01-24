package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurate.R
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.databinding.FragmentFeedContentBinding
import io.github.webcurate.interfaces.OnPageFinish
import io.github.webcurate.options.OptionMenu


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
        _binding!!.contentList.layoutManager = LinearLayoutManager(requireContext())
        _binding!!.contentList.adapter = FeedContentAdapter()


    } // on view


    override fun onDestroyView() {
        super.onDestroyView()
        // set option menu context to feed going back to feed
        OptionMenu.feedItemVisible = false
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED

        _binding = null
        CustomTitle.resetTitle()
    }

    override fun onPageFinished() {
        stateFinish = true
    }


}

class FeedContentAdapter : RecyclerView.Adapter<FeedContentAdapter.ViewHolder>() {

    private val itemList = mutableListOf<String>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.content_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.feed_content_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemText.text = itemList[position]
    }

    override fun getItemCount() = itemList.size

    fun addItems(items: MutableList<String>) {
        itemList.addAll(items)
        notifyDataSetChanged()
    }
}