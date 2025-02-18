package com.jetchan.dev.src.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.src.Task

class TaskAdapter(private var dataList: ArrayList<Task>) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: android.widget.TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataList[position].name
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newData: ArrayList<Task>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }
}