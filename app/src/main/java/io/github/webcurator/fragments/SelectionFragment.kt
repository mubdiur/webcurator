package io.github.webcurator.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurator.R
import io.github.webcurator.clients.CustomTitle
import io.github.webcurator.databases.DataProcessor
import io.github.webcurator.databases.DatabaseClient
import io.github.webcurator.databases.models.*
import io.github.webcurator.databinding.FragmentSelectionBinding
import io.github.webcurator.interfaces.OnItemClick
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
    private var _contentList: MutableList<FeedContent>? = null
    private var _allElements: Elements? = null

    // Not null
    private val selectionSet get() = _selectionSet!!
    private val binding get() = _binding!!
    private val db get() = _db!!
    private val contentList get() = _contentList!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }


        // Initializations
        _binding = FragmentSelectionBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())
        _contentList = mutableListOf()
        _selectionSet = mutableSetOf()
        _allElements = Elements()
        // RecyclerView for selection of texts
        binding.selectionView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectionView.adapter = SelectionAdapter(contentList, this)


        CustomTitle.setTitle("Create Feed - Selection")

        binding.selectionFinish.setOnClickListener {


            CoroutineScope(Dispatchers.IO).launch {
                try {

                    // get feed title and description
                    val feedTitle = db.valueDao().getValue("title")
                    val feedDescription = db.valueDao().getValue("description")
                    // generate queries and get url for the site
                    // val queries = Curator.generateQueries(_allElements!!, selectionSet.sorted().toList())

                    val queries = DataProcessor.getSiteQueries(getSelected())
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
                            if (db.queryDao().countByPathSiteId(query, siteId) < 1) {
                                // no chance of duplicate
                                db.queryDao().insert(SiteQuery(query, siteId))
                            }
                        }
                    }
                    // if the site exists create a feed with this site
                    else {
                        val siteId = db.siteDao().getSiteByUrl(url).siteId
                        val feedId = db.feedDao().insertFeed(Feed(feedTitle, feedDescription))
                        db.feedSitesDao().insert(FeedSites(feedId, siteId))
                        // query insertion
                        queries.forEach { query ->
                            if (db.queryDao().countByPathSiteId(query, siteId) < 1) {
                                // no chance of duplicate
                                db.queryDao().insert(SiteQuery(query, siteId))
                            }
                        }
                    }
                    // INSERT QUERIES


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                withContext(Dispatchers.Main) {
                    CustomTitle.resetTitle()
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
                _html = DataProcessor.html
                contentList.addAll(
                    DataProcessor.getContentsFromElements(
                        Jsoup.parse(_html!!).body().allElements
                    )
                )
            } catch (e: Exception) {
            }
            updateUi()
        }

    }

    private fun getSelected(): List<FeedContent> {
        val selectedContents = mutableListOf<FeedContent>()
        selectionSet.forEach {
            selectedContents.add(contentList[it])
        }
        return selectedContents
    }

    private suspend fun updateUi() {
        withContext(Dispatchers.Main) {
            binding.selectionView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CustomTitle.pop()
        _contentList = null
        _html = null
        _binding = null
        _db = null
        _selectionSet = null
    }

    override fun onItemClicked(position: Int) {
        if (contentList[position].selected) {
            contentList[position].selected = false
            selectionSet.remove(position)
        } else {
            contentList[position].selected = true
            selectionSet.add(position)
        }
        binding.selectionView.adapter?.notifyItemChanged(position)
    }
}


// recyclerview adapter

class SelectionAdapter(
    private val contentList: List<FeedContent>,
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_text_select)
        val slimPath: TextView = view.findViewById(R.id.item_slim_path)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = contentList[position].text
        val shortPath = "Short Path: ${contentList[position].slimPath.joinToString(" > ")}"
        holder.slimPath.text = shortPath
        holder.checkBox.isChecked = contentList[position].selected
        holder.itemView.setOnClickListener {
            onItemClick.onItemClicked(position)
        }
    }

    override fun getItemCount() = contentList.size
}