package com.somanath.example.ottsample.ui.main.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.somanath.example.ottsample.R
import com.somanath.example.ottsample.ui.main.`interface`.ICallBack
import com.somanath.example.ottsample.ui.main.data_model.Content
import com.somanath.example.ottsample.ui.main.utility.ListContentUtil
import com.somanath.example.ottsample.ui.main.view.presenter.OTTSampleAdapter
import com.somanath.example.ottsample.ui.main.view_model.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(), ICallBack {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private  var mAdapter : OTTSampleAdapter ? = null
    private var lastVisibleItemPosition = 0
    private var  pageCounter = 0
    private var visibleThreshHold = 6
    private var totalItemCount = 0
    private var isLoading = false
    private var findMatchAdapter: OTTSampleAdapter? = null
    private lateinit var searchView : SearchView
    private lateinit var actionBar : ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val toolBar = view.findViewById<Toolbar>(R.id.toolBar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolBar)
        actionBar =  (activity as? AppCompatActivity)?.supportActionBar!!
        actionBar.show()
        showBackButton(true)
    }

    private fun showBackButton(isShow: Boolean){
        actionBar.setDisplayHomeAsUpEnabled(isShow)
        actionBar.setHomeButtonEnabled(isShow)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(activity!!.application)
            .create(MainViewModel::class.java)
        initializeViewItems()
        viewModel.getPageData(pageCounter++)
    }

   private fun initializeViewItems(){
       viewModel.getVideosData().observe(viewLifecycleOwner, Observer {
           if (mAdapter == null) {
               mAdapter = OTTSampleAdapter(it, this, false)
               lastVisibleItemPosition += it.size
               contentRecycler.adapter = mAdapter
           } else {
               mAdapter!!.updateData(it)
               isLoading = false
           }
       })
       addScrollListeners()
       addMatchingItemListener()
    }

    private fun addMatchingItemListener(){
        viewModel.matchedVideos.observe(viewLifecycleOwner, {
            if (findMatchAdapter == null) {
                findMatchAdapter = OTTSampleAdapter(it, this, true)
                findMatchAdapter!!.query = searchView.query.toString()
                searchItemRecycler.adapter = findMatchAdapter
                contentRecycler.visibility = View.GONE
                searchItemRecycler.visibility = View.VISIBLE
            } else {
                findMatchAdapter!!.query = searchView.query.toString()
                findMatchAdapter!!.updateData(it as MutableList<Content>)
                contentRecycler.visibility = View.GONE
                searchItemRecycler.visibility = View.VISIBLE
            }
        })
    }
    private fun addScrollListeners(){

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                totalItemCount = layoutManager.itemCount
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (totalItemCount < lastVisibleItemPosition + visibleThreshHold
                    && pageCounter < ListContentUtil.MAX_PAGE_COUNT) {
                    viewModel.getPageData(pageCounter++)
                    isLoading = true
                }
                if (pageCounter == ListContentUtil.MAX_PAGE_COUNT)
                    contentRecycler.removeOnScrollListener(this)
            }
        }
        contentRecycler.addOnScrollListener(scrollListener)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.category_menu, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
         searchView = searchItem.actionView as SearchView
        val closeButton = searchView.findViewById<ImageView>(R.id.search_close_btn)
        closeButton.setOnClickListener(View.OnClickListener {
            if (searchView.query?.length == 0) {
                contentRecycler.visibility = View.VISIBLE
                searchItemRecycler.visibility = View.GONE
                searchView.clearFocus()
                searchView.onActionViewCollapsed()
            } else {
                searchView.setQuery(null, true)
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                showBackButton(true)
                return true
            }

        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                showBackButton(false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                showBackButton(true)
                return true
            }
        })



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.length >= ListContentUtil.MIN_NUMBER_OF_CHAR_TO_QUERY) {
                    findMatchAdapter?.query = null
                    viewModel.findMatch(newText)
                    return true
                } else {
                    searchItemRecycler.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.length >= ListContentUtil.MIN_NUMBER_OF_CHAR_TO_QUERY) {
                    viewModel.findMatch(query)
                    return true
                } else {
                    searchItemRecycler.visibility = View.GONE
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.app_bar_search){
            showBackButton(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            contentRecycler.layoutManager = GridLayoutManager(context, 3)
        } else {
            contentRecycler.layoutManager = GridLayoutManager(context, 7)
        }
    }
}