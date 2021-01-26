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
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        binding!!.recentList.layoutManager = LinearLayoutManager(requireContext())
        val listAdapter = RecentListAdapter(this@HomeFragment, binding!!)
        binding!!.recentList.adapter = listAdapter

        NetEvents.topicEvents.observe(requireActivity(), {
            if(it==NetEvents.TOPIC_READY) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.topicEvents.value = NetEvents.DEFAULT
                }
                Firebase.messaging.subscribeToTopic(Repository.topic)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            println(task.exception?.message.toString())
                        }
                    }
            }
        })


        NetEvents.authEvents.observe(requireActivity(), {
            if (it == NetEvents.TOKEN_READY) {
                updateTopCard(
                    monthName(month),
                    day.toString(),
                    AuthManager.authInstance.currentUser?.displayName.toString()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getTopic()
                }
            }
        })
        NetEvents.feedEvents.observe(requireActivity(), {
            if (it == NetEvents.FEEDS_READY) {
                println("Feeds are ready in Home")
                if(DataProcessor.totalUpdateCount()>0) {
                    binding!!.markRead.visibility = View.VISIBLE
                } else {
                    binding!!.markRead.visibility = View.INVISIBLE
                }
                binding!!.homeUpdateNo.text = DataProcessor.totalUpdateCount().toString()
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.feedEvents.value = NetEvents.DEFAULT
                    listAdapter.updateItems(Repository.feedList)
                }
            }
        })

        binding!!.markRead.setOnClickListener {
             CoroutineScope(Dispatchers.IO).launch {
                 Repository.markAllRead()
             }
        }
    } // on view created

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

    override fun onItemClicked(position: Int, action: Int) {
        MainActivity.nullBinding!!.viewPager.currentItem = 1
        CustomTitle.setTitle(DataProcessor.updatedList.toList()[position].title)
        requireActivity().supportFragmentManager
            .popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        OptionMenu.feedItemVisible = true
        DataProcessor.currentFeed = DataProcessor.updatedList.toList()[position]
        requireActivity().supportFragmentManager.commit {
            replace<FeedContentFragment>(R.id.feedsFragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}


class RecentListAdapter(private val callBack: OnItemClick, private val binding: FragmentHomeBinding) :
    RecyclerView.Adapter<RecentListAdapter.ViewHolder>() {

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
        holder.titleText.text = DataProcessor.updatedList.toList()[position].title
        holder.descriptionText.text = DataProcessor.updatedList.toList()[position].description
        val updateText = DataProcessor.updatedList.toList()[position].updates.toString() + " updates"
        holder.updateText.text = updateText
        holder.itemView.setOnClickListener {
            callBack.onItemClicked(position)
        }
    }

    override fun getItemCount() = DataProcessor.updatedList.size

    fun updateItems(newList: Set<FeedResponse>) {
        DataProcessor.updatedList.clear()
        for (feed in newList) {
            if (feed.updates != 0) {
                DataProcessor.updatedList.add(feed)
            }
        }
        if(DataProcessor.updatedList.isEmpty()) {
            binding.listCover.visibility = View.VISIBLE
        } else {
            binding.listCover.visibility = View.GONE
        }
        notifyDataSetChanged()
    }
}