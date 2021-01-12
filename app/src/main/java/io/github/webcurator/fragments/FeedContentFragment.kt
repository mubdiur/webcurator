package io.github.webcurator.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.webcurator.R
import io.github.webcurator.clients.CustomTitle
import io.github.webcurator.clients.HiddenWeb
import io.github.webcurator.clients.WebJsClient
import io.github.webcurator.databases.DataProcessor
import io.github.webcurator.databases.DatabaseClient
import io.github.webcurator.databinding.FragmentFeedContentBinding
import io.github.webcurator.interfaces.OnPageFinish
import io.github.webcurator.options.OptionMenu
import kotlinx.coroutines.*
import org.jsoup.Jsoup


class FeedContentFragment : Fragment(R.layout.fragment_feed_content), OnPageFinish {


    companion object {
        fun addSite(fragmentManager: FragmentManager) {
            TODO()
        }
        fun editFeed(fragmentManager: FragmentManager) {
            TODO()
        }
        fun manageSites(fragmentManager: FragmentManager) {
            TODO()
        }
        fun deleteFeed() {
            TODO()
        }
        fun toggleNotification() {
            TODO()
        }
    }



    private var _binding: FragmentFeedContentBinding? = null
    private var _db: DatabaseClient? = null
    private var stateFinish = true





    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        _binding = FragmentFeedContentBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())

        // set option menu context to feed item coming from feed
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED_ITEM
        if (_db != null && _binding != null) {

            _binding!!.contentList.layoutManager = LinearLayoutManager(requireContext())
            _binding!!.contentList.adapter = FeedContentAdapter()

            val web = WebView(requireContext())
            web.webViewClient = HiddenWeb
            web.settings.javaScriptEnabled = true
            web.clearCache(true)
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            web.addJavascriptInterface(WebJsClient(this), "WebJsClient")

            CoroutineScope(Dispatchers.IO).launch {
                // get feedId from database value
                if (_db == null) return@launch
                val feedId = _db!!.valueDao().getValue("feedId").toLong()
                // fetch list of siteId with this id
                if (_db == null) return@launch
                val siteIdList = _db!!.feedSitesDao().getSitesByFeedId(feedId)
                siteIdList.forEach { siteId ->
                    val site = _db!!.siteDao().getSite(siteId)
                    val queries = _db!!.queryDao().getQueriesBySiteId(siteId)

                    DataProcessor.html = ""
                    stateFinish = false
                    withContext(Dispatchers.Main) {
                        web.loadUrl(site.url)
                    }
                    println("loading...")
                    var totalTimeout = 0L
                    var timeoutReached = false
                    while (!stateFinish) {
                        if (totalTimeout >= 10000) {
                            timeoutReached = true
                            break
                        }
                        delay(200)
                        totalTimeout += 200L
                    }
                    if (!timeoutReached) println("finished!!!")
                    else println("timeout reached!!!")
                    val html = DataProcessor.html
                    println("parsed html of size: ${html.length}")
                    val elements =
                        withContext(Dispatchers.Default) { Jsoup.parse(html).allElements }
                    val contents = withContext(Dispatchers.Default) {
                        DataProcessor.getContentsFromElements(
                            elements
                        )
                    }
                    val filteredContents = withContext(Dispatchers.Default) {
                        DataProcessor.filterContentStrings(
                            contents,
                            queries
                        ).take(50).toMutableList()
                    }
                    withContext(Dispatchers.Main) {
                        if (_binding != null) (_binding!!.contentList.adapter as FeedContentAdapter)
                            .addItems(filteredContents)
                    }
                }
            }

        }

    } // on view


    override fun onDestroyView() {
        super.onDestroyView()
        // set option menu context to feed going back to feed
        OptionMenu.feedItemVisible = false
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED

        _binding = null
        _db = null
        CustomTitle.resetTitle()
    }

    override fun onPageFinished() {
        stateFinish = true
    }


}

class FeedContentAdapter : RecyclerView.Adapter<FeedContentAdapter.ViewHolder>() {

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