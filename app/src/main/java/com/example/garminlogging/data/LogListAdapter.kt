package com.example.garminlogging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.garminlogging.garmin.GarminLog

class LogListAdapter() :
    ListAdapter<GarminLog, LogListAdapter.ViewHolder>(LogDiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.log_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = getItem(position).message
    }
}

object LogDiffCallback : DiffUtil.ItemCallback<GarminLog>() {
    override fun areItemsTheSame(oldItem: GarminLog, newItem: GarminLog): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: GarminLog, newItem: GarminLog): Boolean {
        return oldItem.message == newItem.message
    }
}