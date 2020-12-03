package com.somanath.example.ottsample.ui.main.view.presenter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.somanath.example.ottsample.R
import com.somanath.example.ottsample.ui.main.`interface`.ICallBack
import com.somanath.example.ottsample.ui.main.data_model.Content
import com.somanath.example.ottsample.ui.main.utility.ListContentUtil
import java.util.*

class OTTSampleAdapter(items: MutableList<Content>, callback: ICallBack, isSearchAdapter : Boolean)
    : RecyclerView.Adapter<OTTSampleAdapter.OTTViewHolder>() {
    var mItems  = items
    var iCallBack = callback
    private lateinit var  mContext : Context
    val isSearchSuggestionAdapter = isSearchAdapter
    var query : String ? = null

     class OTTViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val name : TextView
            get() = itemView.findViewById<TextView>(R.id.name)
            val image : ImageView
            get() = itemView.findViewById<ImageView>(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OTTViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false)
        return OTTViewHolder(view)
    }

    override fun onBindViewHolder(holder: OTTViewHolder, position: Int) {
        if(position == RecyclerView.INVALID_TYPE) return
        if(isSearchSuggestionAdapter && query != null) {
            var stringValue = mItems[position].name
            val builder : SpannableStringBuilder = SpannableStringBuilder(stringValue)
            stringValue = stringValue.toLowerCase(java.util.Locale.ROOT)
            val indexOfQuery = stringValue.indexOf(query!!)
            if (indexOfQuery >= 0) {
                builder.setSpan(
                    ForegroundColorSpan(Color.YELLOW),
                    indexOfQuery, query!!.length + indexOfQuery, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            holder.name.setText(builder,TextView.BufferType.SPANNABLE)
        } else
        holder.name.text = mItems[position].name
        ListContentUtil.glideImageToView(mContext,holder.image,
            mItems[position].posterImage.substring(0,7))
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateData(newItems: List<Content>) {
        if (isSearchSuggestionAdapter) {
            mItems.clear()
            mItems.addAll(newItems)
            notifyDataSetChanged()
        } else {
            var startIndex = itemCount - 1
            mItems.addAll(newItems)
            notifyItemRangeInserted(startIndex, newItems.size)
        }
    }

}