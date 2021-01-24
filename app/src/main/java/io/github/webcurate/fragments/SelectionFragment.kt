package io.github.webcurate.fragments

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
import io.github.webcurate.R
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.models.FeedContent
import io.github.webcurate.databinding.FragmentSelectionBinding
import io.github.webcurate.interfaces.OnItemClick
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.networking.models.FeedRequest
import io.github.webcurate.networking.models.SiteRequest
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
    private var _html: String? = null
    private var _contentList: MutableList<FeedContent>? = null
    private var _allElements: Elements? = null

    // Not null
    private val selectionSet get() = _selectionSet!!
    private val binding get() = _binding!!
    private val contentList get() = _contentList!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }


        // Initializations
        _binding = FragmentSelectionBinding.bind(view)
        _contentList = mutableListOf()
        _selectionSet = mutableSetOf()
        _allElements = Elements()
        // RecyclerView for selection of texts
        binding.selectionView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectionView.adapter = SelectionAdapter(contentList, this)

       if (DataProcessor.siteModifyMode) {
           CustomTitle.setTitle("Modify Site - Selection")
       } else {
           CustomTitle.setTitle("Create Feed - Selection")
       }

        binding.selectionFinish.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val queries = DataProcessor.getSiteQueries(getSelected()).toList()
                withContext(Dispatchers.IO) {
                    if(DataProcessor.siteModifyMode) {
                        Repository.deleteSite(DataProcessor.currentSite!!.id)
                        Repository.insertOneSite(
                            DataProcessor.currentFeed!!.id,
                            SiteRequest(
                                DataProcessor.feedCreationUrl,
                                queries
                            )
                        )
                    } else {
                        Repository.insertFeed(
                            FeedRequest(
                                DataProcessor.feedCreationTitle,
                                DataProcessor.feedCreationDescription,
                                listOf(
                                    SiteRequest(
                                        DataProcessor.feedCreationUrl,
                                        queries
                                    )
                                )
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        CustomTitle.resetTitle()
                    }
                    requireActivity().supportFragmentManager
                        .popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                } // end of with context

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
        selectionSet.sorted().forEach {
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
        _selectionSet = null
    }

    override fun onItemClicked(position: Int, action: Int) {
        if (contentList.toList()[position].selected) {
            contentList.toList()[position].selected = false
            selectionSet.remove(position)
        } else {
            contentList.toList()[position].selected = true
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
        holder.textView.text = contentList.toList()[position].text
        val shortPath = "Short Path: ${contentList.toList()[position].slimPath.joinToString(" > ")}"
        holder.slimPath.text = shortPath
        holder.checkBox.isChecked = contentList.toList()[position].selected
        holder.itemView.setOnClickListener {
            onItemClick.onItemClicked(position)
        }
    }

    override fun getItemCount() = contentList.size
}