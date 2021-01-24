package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
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
import io.github.webcurate.databinding.FragmentFeedEditBinding
import io.github.webcurate.interfaces.OnItemClick
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.options.OptionMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedEditFragment : Fragment(R.layout.fragment_feed_edit), OnItemClick {

    private var binding: FragmentFeedEditBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        binding = FragmentFeedEditBinding.bind(view)

        // Set the Option Menu Context to default
        OptionMenu.contextType = OptionMenu.CONTEXT_DEFAULT

        binding!!.feedTitle.setText(DataProcessor.currentFeed!!.title, TextView.BufferType.EDITABLE)
        binding!!.feedDescription.setText(
            DataProcessor.currentFeed!!.description,
            TextView.BufferType.EDITABLE
        )


        binding!!.saveButton.setOnClickListener {
            hideKeyboard()
            if (binding!!.feedDescription.text!!.isEmpty()) {
                binding!!.feedDescription.requestFocus()
                binding!!.feedDescription.error = "This field cannot be blank"
            }
            if (binding!!.feedTitle.text!!.isEmpty()) {
                binding!!.feedTitle.requestFocus()
                binding!!.feedTitle.error = "This field cannot be blank"
            }
            if (
                binding!!.feedDescription.text!!.isNotEmpty()
                && binding!!.feedTitle.text!!.isNotEmpty()
            ) {
                if (
                    !binding!!.feedTitle.text!!.equals(DataProcessor.currentFeed!!.title)
                    || !binding!!.feedDescription.text!!.equals(DataProcessor.currentFeed!!.description)
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        Repository.modifyFeed(
                            DataProcessor.currentFeed!!.id,
                            binding!!.feedTitle.text.toString(),
                            binding!!.feedDescription.text.toString()
                        )

                    }
                }
            }
            requireActivity().supportFragmentManager
                .popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

        } // on click

        /**
         * Site List
         */
        val siteListAdapter = SiteListAdapter(this@FeedEditFragment)
        binding!!.siteList.layoutManager = LinearLayoutManager(requireContext())
        binding!!.siteList.adapter = siteListAdapter
        NetEvents.siteEvents.observe(requireActivity(), {
            if (it == NetEvents.SITES_READY) {
                println("Sites ready")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.siteEvents.value = NetEvents.DEFAULT
                    siteListAdapter.notifyDataSetChanged()
                }
            }
            if (it == NetEvents.UPDATE_SITES) {
                println("Update sites")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.siteEvents.value = NetEvents.DEFAULT
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getSitesForFeed(DataProcessor.currentFeed!!.id)
                }
            }
            if (it == NetEvents.SITE_DELETED) {
                println("Site deleted")
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.siteEvents.value = NetEvents.DEFAULT
                }

                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getSitesForFeed(DataProcessor.currentFeed!!.id)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.contentEvents.value = NetEvents.CURATE_CONTENTS
                    }

                }
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            Repository.getSitesForFeed(DataProcessor.currentFeed!!.id)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED_ITEM
        binding = null
        Repository.siteList.clear()
        NetEvents.siteEvents.removeObservers(requireActivity())
        CustomTitle.pop()
    }

    private fun hideKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onItemClicked(position: Int, action: Int) {
        when (action) {
            1 -> { // delete
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.deleteSite(Repository.siteList.toList()[position].id)
                }
            }
            2 -> { // modify
                DataProcessor.siteModifyMode = true
                DataProcessor.currentSite = Repository.siteList.toList()[position]

                requireActivity().supportFragmentManager.commit {
                    replace<PageFragment>(R.id.feedEditFragment, tag = "pageFragment")
                    setReorderingAllowed(true)
                    addToBackStack(null)
                }
            }
        }
    }


}

class SiteListAdapter(private val callback: OnItemClick) :
    RecyclerView.Adapter<SiteListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlView: TextView = itemView.findViewById(R.id.urlView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val modifyButton: ImageButton = itemView.findViewById(R.id.modifyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.site_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.urlView.text = Repository.siteList.toList()[position].url
        holder.deleteButton.setOnClickListener {
            callback.onItemClicked(position, 1)
        }
        holder.modifyButton.setOnClickListener {
            callback.onItemClicked(position, 2)
        }
    }

    override fun getItemCount() = Repository.siteList.size
}