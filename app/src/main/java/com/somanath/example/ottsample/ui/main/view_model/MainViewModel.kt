package com.somanath.example.ottsample.ui.main.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.somanath.example.ottsample.ui.main.data_model.Content
import com.somanath.example.ottsample.ui.main.data_model.OTTVideos
import com.somanath.example.ottsample.ui.main.utility.ListContentUtil
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executor

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var videos = MutableLiveData<MutableList<Content>>()
    var matchedVideos = MutableLiveData<MutableList<Content>>()
    val gson = Gson()

    fun getVideosData():MutableLiveData<MutableList<Content>>{
        return videos
    }

    fun getPageData(pageNum : Int){
        viewModelScope.launch {
            val page = ListContentUtil.getJsonFromAssets(getApplication(),getPageName(pageNum))
            val videoPage =  gson.fromJson(page,OTTVideos::class.java)
            videos.value = videoPage.page.contentItems.content
        }
    }

    private fun getPageName(pageNum: Int) = when (pageNum) {
        0 -> ListContentUtil.FILE_NAME_PAGE1
        1 -> ListContentUtil.FILE_NAME_PAGE2
        else -> ListContentUtil.FILE_NAME_PAGE3
    }

    fun findMatch(query : String) {
        viewModelScope.launch {
           matchedVideos.value?.clear()
            val matchingList = videos.value?.filter {
                it.name.contains(query) || it.name.toLowerCase(Locale.ROOT).contains(query)
            }
            matchedVideos.value = matchingList as MutableList<Content>
        }
    }
}