package com.jetchan.dev.src.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.src.OrganizationBaseInfo
import com.jetchan.dev.src.Task

class OrgTaskAdapter(private var dataList: ArrayList<Task>,
                     private var onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<OrgTaskAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(taskInfo: Task)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: android.widget.TextView = itemView.findViewById(R.id.tv_task_name)
        val statusTextView: android.widget.TextView = itemView.findViewById(R.id.tv_task_status)
        val publisherTextView: android.widget.TextView = itemView.findViewById(R.id.tv_task_publisher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_org_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = dataList[position].name
        holder.statusTextView.text = dataList[position].state
        holder.publisherTextView.text = dataList[position].publisherId.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(dataList[position])
        }
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