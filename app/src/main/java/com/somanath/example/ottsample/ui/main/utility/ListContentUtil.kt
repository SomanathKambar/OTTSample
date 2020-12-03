package com.somanath.example.ottsample.ui.main.utility

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.somanath.example.ottsample.R
import java.io.IOException

class ListContentUtil {

    companion object{
        const val FILE_NAME_PAGE1 = "page1.json"
        const val FILE_NAME_PAGE2 = "page2.json"
        const val FILE_NAME_PAGE3 = "page3.json"
        const val MIN_NUMBER_OF_CHAR_TO_QUERY = 3
        const val MAX_PAGE_COUNT = 3

        fun getJsonFromAssets(context: Context, fileName: String): String? {
            val json: String?
            try {
                json = context.assets.open(fileName).bufferedReader().use {
                    it.readText()
                }
            } catch (e: IOException) {
                return null
            }
            return json
        }

        fun glideImageToView(context: Context,target : ImageView,imageType: String){
            Glide.with(context).asDrawable().load(getPosterID(imageType)).
            placeholder(R.drawable.placeholder_for_missing_posters).
            into(target)
        }

        fun getPosterID(imageType: String) = when(imageType){
            "poster1" -> R.drawable.poster1
            "poster2" -> R.drawable.poster2
            "poster3" -> R.drawable.poster3
            "poster4" -> R.drawable.poster4
            "poster5" -> R.drawable.poster5
            "poster6" -> R.drawable.poster6
            "poster7" -> R.drawable.poster7
            "poster8" -> R.drawable.poster8
            "poster9" -> R.drawable.poster9
            else -> R.drawable.placeholder_for_missing_posters
        }
    }
}
