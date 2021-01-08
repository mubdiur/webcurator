package com.mubdiur.webcurator.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.Fuel
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.databases.Curator
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databases.models.HtmlData
import com.mubdiur.webcurator.databinding.FragmentFeedContentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


class FeedContentFragment: Fragment(R.layout.fragment_feed_content) {
    private var _binding: FragmentFeedContentBinding? = null
    private var _db: DatabaseClient? = null

    private val binding get() = _binding!!
    private val db get() = _db!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        _binding = FragmentFeedContentBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())

        binding.contentList.layoutManager = LinearLayoutManager(requireContext())
        binding.contentList.adapter = FeedContentAdapter()

        CoroutineScope(Dispatchers.IO).launch {
            // get feedId from database value
            val feedId = db.valueDao().getValue("feedId").toLong()
            // fetch list of siteId with this id
            val siteIdList = db.feedSitesDao().getSitesByFeedId(feedId)
            // for each site id  fetch the site then html from url and get contents from querying as  list of strings
            siteIdList.forEach { siteId ->
                // get the site for this siteId
                val site = db.siteDao().getSite(siteId)
                // get queries for this siteId
                val queries = db.queryDao().getQueriesBySiteId(siteId)
                Fuel.post("https://mubdiur.com:8321", listOf("url" to site.url))
                    .responseString { result ->
                        val html = Klaxon().parse<HtmlData>(result.component1().toString())?.html
                        (binding.contentList.adapter as FeedContentAdapter)
                            .addItems(Curator.getContents(Jsoup.parse(html).allElements, queries)
                                .toMutableList())
                    }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _db = null
    }

}

class FeedContentAdapter: RecyclerView.Adapter<FeedContentAdapter.ViewHolder>() {

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