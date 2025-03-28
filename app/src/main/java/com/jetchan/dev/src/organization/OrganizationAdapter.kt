package com.jetchan.dev.src.organization

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.src.OrganizationBaseInfo

class OrganizationAdapter(private val context: Context,
                          private var dataList: ArrayList<OrganizationBaseInfo>,
                          private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<OrganizationAdapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(orgInfo: OrganizationBaseInfo)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orgName: android.widget.TextView = itemView.findViewById(R.id.tv_org_name)
        val orgType: android.widget.ImageView = itemView.findViewById(R.id.v_org_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_organization, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orgName.text = dataList[position].name
        when (dataList[position].type) {
            "family" -> {
                val drawable = ContextCompat.getDrawable(context, R.drawable.circle_family)
                // 设置背景
                drawable?.let {
                    holder.orgType.background = it
                }
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newData: ArrayList<OrganizationBaseInfo>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }


}