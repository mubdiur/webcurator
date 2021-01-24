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
import io.github.webcurate.data.AuthManager
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.FragmentHomeBinding
import io.github.webcurate.interfaces.OnItemClick
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.networking.models.FeedResponse
import io.github.webcurate.options.OptionMenu
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home), OnItemClick {

    private var binding: FragmentHomeBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        binding = FragmentHomeBinding.bind(view)

        binding!!.rootView.visibility = View.INVISIBLE
        MainActivity.startLoadingAnimation()
        val cal: Calendar = Calendar.getInstance()
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val month: Int = cal.get(Calendar.MONTH)

        val authObserver = Observer<Int> {
            if (it == NetEvents.TOKEN_READY) {
                updateTopCard(
                    monthName(month),
                    day.toString(),
                    AuthManager.authInstance.currentUser?.displayName.toString()
                )

            }
        }

        binding!!.recentList.layoutManager = LinearLayoutManager(requireContext())
        val listAdapter = RecentListAdapter(this@HomeFragment)
        binding!!.recentList.adapter = listAdapter

        val feedsObserver = Observer<Int> {
            if (it == NetEvents.FEEDS_READY) {
                binding!!.homeUpdateNo.text = DataProcessor.totalUpdateCount().toString()
                listAdapter.updateItems(Repository.feedList)
            }
        }
        NetEvents.authEvents.observe(requireActivity(), authObserver)
        NetEvents.feedEvents.observe(requireActivity(), feedsObserver)


    }

    private fun monthName(month: Int): String {
        return when (month) {
            0 -> "Jan"
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "Aug"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            else -> "Dec"
        }
    }

    private fun updateTopCard(month: String, day: String, name: String) {
        if (binding != null) {
            binding!!.monthName.text = month
            binding!!.dayNumber.text = day
            binding!!.homeNameText.text = name
            binding!!.rootView.visibility = View.VISIBLE
            MainActivity.stopLoadingAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onItemClicked(position: Int) {
        MainActivity.nullBinding!!.viewPager.currentItem = 1
        CustomTitle.setTitle(DataProcessor.updatedList[position].title)
        requireActivity().supportFragmentManager
            .popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        OptionMenu.feedItemVisible = true
        DataProcessor.currentFeedId = DataProcessor.updatedList[position].id
        requireActivity().supportFragmentManager.commit {
            replace<FeedContentFragment>(R.id.feedsFragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}


class RecentListAdapter(private val callBack: OnItemClick): RecyclerView.Adapter<RecentListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionText: TextView = itemView.findViewById(R.id.feed_description)
        val updateText: TextView = itemView.findViewById(R.id.updateView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_root_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = DataProcessor.updatedList[position].title
        holder.descriptionText.text = DataProcessor.updatedList[position].description
        val updateText = DataProcessor.updatedList[position].updates.toString() + " updates"
        holder.updateText.text =  updateText
        holder.itemView.setOnClickListener {
            callBack.onItemClicked(position)
        }
    }

    override fun getItemCount() = DataProcessor.updatedList.size

    fun updateItems(newList: List<FeedResponse>) {
        DataProcessor.updatedList.clear()
        for(feed in newList) {
            if(feed.updates!=0) {
                DataProcessor.updatedList.add(feed)
            }
        }
        notifyDataSetChanged()
    }
}