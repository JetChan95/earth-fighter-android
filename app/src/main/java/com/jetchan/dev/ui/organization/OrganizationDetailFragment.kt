package com.jetchan.dev.ui.organization

import OrganizationDetailViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jetchan.dev.R

class OrganizationDetailFragment : Fragment() {

    private lateinit var viewModel: OrganizationDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载 Fragment 的布局
        val view = inflater.inflate(R.layout.fragment_org_detail, container, false)

        val idTextView: TextView = view.findViewById(R.id.tv_org_id)
        val nameTextView: TextView = view.findViewById(R.id.tv_org_name)
        val typeTextView: TextView = view.findViewById(R.id.tv_org_type)
        val invitCodeTextView: TextView = view.findViewById(R.id.tv_org_invite_code)

        // 获取 ViewModel 实例
        viewModel = ViewModelProvider(this)[OrganizationDetailViewModel::class.java]

        // 接收传递的信息
        val id = arguments?.getString("id")
        val name = arguments?.getString("name")

        // 将数据传递给 ViewModel
        id?.let { viewModel.setOrganizationData(it, name ?: "") }

        // 观察 LiveData 的变化并更新视图
        viewModel.orgId.observe(viewLifecycleOwner) {
            idTextView.text = it
        }

        viewModel.orgName.observe(viewLifecycleOwner) {
            nameTextView.text = it
        }

        viewModel.orgType.observe(viewLifecycleOwner) {
            typeTextView.text = it
        }

        viewModel.inviteCode.observe(viewLifecycleOwner) {
            invitCodeTextView.text = it
        }

        return view
    }
}