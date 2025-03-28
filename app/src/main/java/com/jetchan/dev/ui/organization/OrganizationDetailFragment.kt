package com.jetchan.dev.ui.organization

import OrganizationDetailViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentOrgDetailBinding
import com.jetchan.dev.databinding.FragmentOrganizationBinding
import com.jetchan.dev.src.OrganizationBaseInfo
import com.jetchan.dev.src.Task
import com.jetchan.dev.src.organization.OrganizationAdapter
import com.jetchan.dev.src.task.OrgTaskAdapter
import com.jetchan.dev.src.task.TaskAdapter

class OrganizationDetailFragment : Fragment(), OrgTaskAdapter.OnItemClickListener {

    private lateinit var viewModel: OrganizationDetailViewModel
    private lateinit var _binding: FragmentOrgDetailBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrgTaskAdapter
    private lateinit var noOrgTextView: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 获取 ViewModel 实例
        viewModel = ViewModelProvider(this)[OrganizationDetailViewModel::class.java]

        _binding = FragmentOrgDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val idTextView: TextView = root.findViewById(R.id.tv_org_id)
        val nameTextView: TextView = root.findViewById(R.id.tv_org_name)
        val typeTextView: TextView = root.findViewById(R.id.tv_org_type)
        val inviteCodeTextView: TextView = root.findViewById(R.id.tv_org_invite_code)

        noOrgTextView = root.findViewById(R.id.tv_no_task)

        recyclerView = root.findViewById(R.id.rv_org_tasks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrgTaskAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter

        // 接收传递的信息
        val id = arguments?.getInt("id")
        val name = arguments?.getString("name")
        val inviteCode = arguments?.getString("inviteCode")
        val type = arguments?.getString("type")

        // 将数据传递给 ViewModel
        id?.let { viewModel.setOrganizationData(it, name ?: "", type ?: "", inviteCode ?: "") }

        // 观察 LiveData 的变化并更新视图
        viewModel.orgId.observe(viewLifecycleOwner) {
            idTextView.text = it.toString()
        }

        viewModel.orgName.observe(viewLifecycleOwner) {
            nameTextView.text = it
        }

        viewModel.orgType.observe(viewLifecycleOwner) {
            typeTextView.text = it
        }

        viewModel.inviteCode.observe(viewLifecycleOwner) {
            inviteCodeTextView.text = it
        }

        val btnPublishTask: Button = root.findViewById(R.id.btn_publish_task)
        btnPublishTask.setOnClickListener() {
            val bundle = Bundle()
            bundle.putInt("id", viewModel.orgId.value?:-1)
            findNavController().navigate(R.id.action_nav_org_detail_to_nav_org_publish_task, bundle)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.taskList.observe(viewLifecycleOwner) {
            adapter.updateData(it)
            if (it.isEmpty()) {
                binding.tvNoTask.visibility = View.VISIBLE
                binding.rvOrgTasks.visibility = View.GONE
            } else {
                binding.tvNoTask.visibility = View.GONE
                binding.rvOrgTasks.visibility = View.VISIBLE
            }
        }
        viewModel.loadTasks(requireContext())
    }

    override fun onItemClick(taskInfo: Task) {
        // 创建 Bundle 用于传递数据
        val bundle = Bundle()
        bundle.putString("task", Gson().toJson(taskInfo))

        findNavController().navigate(R.id.action_nav_org_detail_to_nav_org_task_detail, bundle)
    }
}