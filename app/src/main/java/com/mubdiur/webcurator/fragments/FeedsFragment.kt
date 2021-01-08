package com.mubdiur.webcurator.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.activities.MainActivity
import com.mubdiur.webcurator.clients.ContentStatus
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databases.models.Feed
import com.mubdiur.webcurator.databases.models.Value
import com.mubdiur.webcurator.databinding.FragmentFeedsBinding
import com.mubdiur.webcurator.interfaces.OnItemClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FeedsFragment : Fragment(R.layout.fragment_feeds), OnItemClick {

    private var _binding: FragmentFeedsBinding? = null
    private var _db: DatabaseClient? = null
    private var _feedList: List<Feed>? = null

    private val binding get() = _binding!!
    private val db get() = _db!!
    private val feedList get() = _feedList!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        // initialization
        _binding = FragmentFeedsBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())
        _feedList = listOf()


        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.feedDao().getAllFeeds().collect {
                    if(it.isNotEmpty()) {
                        _feedList = it
                        updateUI(feedList)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "onViewCreated: ${e.message}")
            }
        }


        // RecyclerView
        binding.feedList.layoutManager = LinearLayoutManager(requireContext())
        binding.feedList.adapter = FeedListAdapter(feedList, this)
    }

    private suspend fun updateUI(feedList: List<Feed>) {
        withContext(Dispatchers.Main) {
            binding.feedList.adapter = FeedListAdapter(feedList, this@FeedsFragment)
            binding.feedList.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _db = null
        _feedList = null
    }

    override fun onItemClicked(position: Int) {
        MainActivity.nullBinding?.titleText?.text = feedList[position].feedTitle
        MainActivity.nullBinding?.addButton?.visibility = View.INVISIBLE
        ContentStatus.show = true
        ContentStatus.title = feedList[position].feedTitle
        CoroutineScope(Dispatchers.IO).launch {
            db.valueDao().insertValue(Value("feedId", feedList[position].feedId.toString()))
            requireActivity().supportFragmentManager.commit {
                replace<FeedContentFragment>(R.id.feedsFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }
}


class FeedListAdapter(private val feedList: List<Feed>, private val callBack: OnItemClick) :
    RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedTitle: TextView = itemView.findViewById(R.id.content_text)
        val feedDescription: TextView = itemView.findViewById(R.id.feed_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_root_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.feedTitle.text = feedList[position].feedTitle
        holder.feedDescription.text = feedList[position].feedDescription
        holder.itemView.setOnClickListener {
            callBack.onItemClicked(position)
        }
    }

    override fun getItemCount() = feedList.size
}
