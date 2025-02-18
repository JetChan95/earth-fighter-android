package com.jetchan.dev.ui.organization

import OrganizationDetailViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentOrganizationBinding
import com.jetchan.dev.src.organization.OrganizationAdapter
import com.jetchan.dev.src.task.TaskAdapter

class OrganizationDetailFragment : Fragment() {

    private lateinit var viewModel: OrganizationDetailViewModel
    private lateinit var _binding: FragmentOrganizationBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var noOrgTextView: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        _binding = FragmentOrganizationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = root.findViewById(R.id.rv_organization)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskAdapter(arrayListOf())
        recyclerView.adapter = adapter

        // 接收传递的信息
        val id = arguments?.getInt("id")
        val name = arguments?.getString("name")

        // 将数据传递给 ViewModel
        id?.let { viewModel.setOrganizationData(it, name ?: "") }

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
            invitCodeTextView.text = it
        }

        noOrgTextView = root.findViewById(R.id.no_org_text_view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.taskList.observe(viewLifecycleOwner) {
            adapter.updateData(it)
            if (it.isEmpty()) {
                binding.noOrgTextView.visibility = View.VISIBLE
                binding.rvOrganization.visibility = View.GONE
            } else {
                binding.noOrgTextView.visibility = View.GONE
                binding.rvOrganization.visibility = View.VISIBLE
            }
        }
    }
}