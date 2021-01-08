package com.mubdiur.webcurator.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.databases.Curator
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databases.models.Feed
import com.mubdiur.webcurator.databases.models.FeedSites
import com.mubdiur.webcurator.databases.models.Site
import com.mubdiur.webcurator.databases.models.SiteQuery
import com.mubdiur.webcurator.databinding.FragmentSelectionBinding
import com.mubdiur.webcurator.interfaces.OnItemClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class SelectionFragment : Fragment(R.layout.fragment_selection), OnItemClick {

    // All nullable

    private var _selectionSet: MutableSet<Int>? = null
    private var _binding: FragmentSelectionBinding? = null
    private var _db: DatabaseClient? = null
    private var _html: String? = null
    private var _itemList: MutableList<Item>? = null

    // Not null
    private val selectionSet get() = _selectionSet!!
    private val binding get() = _binding!!
    private val db get() = _db!!
    private val itemList get() = _itemList!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        // Initializations
        _binding = FragmentSelectionBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())
        _itemList = mutableListOf()
        _selectionSet = mutableSetOf()

        // RecyclerView for selection of texts
        binding.selectionView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectionView.adapter = SelectionAdapter(itemList, this)


        binding.selectionFinish.setOnClickListener {


            CoroutineScope(Dispatchers.IO).launch {
                try {

                    // get feed title and description
                    val feedTitle = db.valueDao().getValue("title")
                    val feedDescription = db.valueDao().getValue("description")
                    // generate queries and get url for the site
                    val queries = Curator.generateQueries(_html!!, selectionSet.sorted().toList())
                    val url = db.valueDao().getValue("url")


                    // INSERT SITE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                    // see if the site with this url already exist
                    val count = db.siteDao().countSiteByUrl(url)

                    // if the site does not exist add the site and add the feed with the site relation
                    if (count < 1) {
                        val siteId = db.siteDao().insertSite(Site(url))
                        val feedId = db.feedDao().insertFeed(Feed(feedTitle, feedDescription))
                        db.feedSitesDao().insert(FeedSites(feedId, siteId))
                        // query insertion
                        queries.forEach { query ->
                            if(db.queryDao().countByPathSiteId(query, siteId) < 1) {
                                // no chance of duplicate
                                db.queryDao().insert(SiteQuery(query, siteId))
                            }
                        }
                    }
                    // if the site exists create a feed with this site
                    else {
                        println("testing testing")
                        val siteId = db.siteDao().getSiteByUrl(url).siteId
                        val feedId = db.feedDao().insertFeed(Feed(feedTitle, feedDescription))
                        db.feedSitesDao().insert(FeedSites(feedId, siteId))
                        // query insertion
                        queries.forEach { query ->
                            if(db.queryDao().countByPathSiteId(query, siteId) < 1) {
                                // no chance of duplicate
                                db.queryDao().insert(SiteQuery(query, siteId))
                            }
                        }
                    }
                    // INSERT QUERIES


                } catch (e: Exception) {
                    println("test1: error  : ${e.stackTrace}")
                }
                requireActivity().supportFragmentManager
                    .popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )

            } // Coroutine

        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                _html = db.valueDao().getValue("html")
                updateData(Jsoup.parse(_html!!).body().allElements)
            } catch (e: Exception) {
            }
            updateUi()
        }

    }

    private suspend fun updateData(elements: Elements) {
        withContext(Dispatchers.Default) {
            elements.forEachIndexed { index, element ->
                if (element.ownText().isNotEmpty()) {
                    itemList.add(Item(element.ownText(), index))
                }
            } // end of foreach
        } // end of withContext(Dispatchers.Default)
    } // end of updateData

    private suspend fun updateUi() {
        withContext(Dispatchers.Main) {
            binding.selectionView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _itemList = null
        _html = null
        _binding = null
        _db = null
        _selectionSet = null
    }

    override fun onItemClicked(position: Int) {
        if (itemList[position].isSelected()) {
            itemList[position].unSelect()
            selectionSet.remove(itemList[position].originalIndex)
        } else {
            itemList[position].select()
            selectionSet.add(itemList[position].originalIndex)
        }
        binding.selectionView.adapter?.notifyItemChanged(position)
    }
}

// item model
class Item(private var text: String = "", val originalIndex: Int) {

    private var selected = false
    fun getText() = text

    fun isSelected() = selected
    fun select() {
        selected = true
    }

    fun unSelect() {
        selected = false
    }
}


// recyclerview adapter

class SelectionAdapter(private val itemList: List<Item>, private val onItemClick: OnItemClick) :
    RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_text_select)
        val selectionImg: ImageView = view.findViewById(R.id.tickImg)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position].getText()
        if (itemList[position].isSelected()) {
            holder.selectionImg.visibility = View.VISIBLE
        } else {
            holder.selectionImg.visibility = View.INVISIBLE
        }
        holder.itemView.setOnClickListener {
            onItemClick.onItemClicked(position)
        }
    }

    override fun getItemCount() = itemList.size
}