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

    private var _selectionMap: MutableMap<Int, Boolean>? = null
    private var _binding: FragmentSelectionBinding? = null
    private var _db: DatabaseClient? = null
    private var _html: String? = null
    private var _allElements: Elements? = null
    private var _itemList: MutableList<Item>? = null

    // Not null

    private val selectionMap get() = _selectionMap!!
    private val binding get() = _binding!!
    private val db get() = _db!!
    private val allElements get() = _allElements!!
    private val itemList get() = _itemList!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        // Initializations
        _binding = FragmentSelectionBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())
        _itemList = mutableListOf()
        _selectionMap = mutableMapOf()
        // RecyclerView for selection of texts
        binding.selectionView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectionView.adapter = SelectionAdapter(itemList, this)


        binding.selectionFinish.setOnClickListener {


            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // get the selected elements
                    val selectionList = mutableListOf<Int>()
                    for (i in selectionMap.keys) {
                        if (selectionMap[i] == true) {
                            selectionList.add(i)
                        }
                    }
                    // get feed title and description
                    val feedTitle = db.valueDao().getValue("title")
                    val feedDescription = db.valueDao().getValue("description")
                    // generate query and get url for the site
                    val queries = Curator.generateQueries(allElements, selectionList)
                    val url = db.valueDao().getValue("url")
                    // get the counts of sites with this url and and queries
                    val count = db.siteDao().getCount(url, queries)
                    println("test1: count : $count")
                    // if the site does not exist add the site and add the feed with the site relation
                    if (count < 1) {
                        val siteId = db.siteDao().insertSite(
                            Site(
                                url, queries
                            )
                        )
                        val feedId = db.feedDao().insertFeed(
                            Feed(
                                feedTitle,
                                feedDescription
                            )
                        )
                        db.feedSitesDao().insert(
                            FeedSites(
                                feedId,
                                siteId
                            )
                        )

                    }
                    // if the site exists create a feed with this site
                    else {
                        println("testing testing")
                        val siteId = db.siteDao().getId(url, queries)
                        val feedId = db.feedDao().insertFeed(
                            Feed(
                                feedTitle,
                                feedDescription
                            )
                        )
                        db.feedSitesDao().insert(
                            FeedSites(
                                feedId,
                                siteId
                            )
                        )
                    }
                    // return to feeds fragment


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
            _allElements = elements
            elements.forEach {
                if (it.ownText().isNotEmpty()) {
                    itemList.add(Item(it.ownText()))
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
        _allElements = null
        _html = null
        _binding = null
        _db = null
        _selectionMap = null
    }

    override fun onItemClicked(position: Int) {
        if (itemList[position].isSelected()) {
            itemList[position].unSelect()
            selectionMap[position] = false
        } else {
            itemList[position].select()
            selectionMap[position] = true
        }
        binding.selectionView.adapter?.notifyItemChanged(position)
    }
}

// item model
class Item(private var text: String = "") {

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