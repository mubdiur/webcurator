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
import com.mubdiur.webcurator.clients.DatabaseClient
import com.mubdiur.webcurator.databinding.FragmentSelectionBinding
import com.mubdiur.webcurator.interfaces.OnItemClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class SelectionFragment : Fragment(R.layout.fragment_selection), OnItemClick {

    private val selectionMap = hashMapOf<Int, Boolean>()
    private var _binding: FragmentSelectionBinding? = null
    private var _db: DatabaseClient? = null
    private var html = ""
    private val itemList = mutableListOf<Item>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }


        val binding = FragmentSelectionBinding.bind(view)
        _binding = binding

        val db = DatabaseClient(requireContext())
        _db = db

        binding.selectionView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectionView.adapter = SelectionAdapter(itemList, this)


        binding.selectionNext.setOnClickListener {

            requireActivity().supportFragmentManager
                .popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
        }

        CoroutineScope(Dispatchers.IO).launch {
            html = db.getValue("html")
            updateData(Jsoup.parse(html).body().allElements)
            updateUi()
        }

    }

    private suspend fun updateData(elements: Elements) {
        withContext(Dispatchers.Default) {

            elements.forEach {
                if(it.ownText().isNotEmpty()) {
                    val item = Item()
                    item.setText(it.ownText())
                    itemList.add(item)
                }
            } // end of foreach
        } // end of withContext(Dispatchers.Default)
    } // end of updateData

    private suspend fun updateUi() {
        withContext(Dispatchers.Main) {
            _binding?.selectionView?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        _binding = null
        _db = null
        super.onDestroy()
    }

    override fun onItemClicked(position: Int) {
        if (itemList[position].isSelected()) {
            itemList[position].unSelect()
            selectionMap[position] = false
        } else {
            itemList[position].select()
            selectionMap[position] = true
        }
        _binding?.selectionView?.adapter?.notifyItemChanged(position)
    }
}

// item model
class Item {
    private var text = ""
    private var selected = false
    fun getText() = text
    fun setText(text: String) {
        this.text = text
    }

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