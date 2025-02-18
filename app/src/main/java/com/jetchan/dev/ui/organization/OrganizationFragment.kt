package com.jetchan.dev.ui.organization

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentOrganizationBinding
import com.jetchan.dev.src.OrganizationBaseInfo
import com.jetchan.dev.src.organization.OrganizationAdapter
import com.jetchan.dev.utils.InputFilterUtils
import com.jetchan.dev.utils.SharedPreferencesUtil
import com.jetchan.dev.utils.getCurrentFunctionName
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

class OrganizationFragment : Fragment(), OrganizationAdapter.OnItemClickListener {

    private var _binding: FragmentOrganizationBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrganizationAdapter
    private lateinit var noOrgTextView: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: OrganizationViewModel by lazy {
        ViewModelProvider(this).get(OrganizationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")

        _binding = FragmentOrganizationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = root.findViewById(R.id.rv_organization)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrganizationAdapter(requireContext(), arrayListOf(), this)
        recyclerView.adapter = adapter

        val btnJoinOrg = root.findViewById<Button>(R.id.btn_joinOrg)
        btnJoinOrg.setOnClickListener {
            showJoinOrgDialog()
        }

        val btnCreateOrg = root.findViewById<Button>(R.id.btn_createOrg)
        btnCreateOrg.setOnClickListener {
            showCreateOrgDialog()
        }

        noOrgTextView = root.findViewById(R.id.no_org_text_view)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onViewCreated(view, savedInstanceState)
        viewModel.orgList.observe(viewLifecycleOwner) {
            adapter.updateData(it)
            if (it.isEmpty()) {
                binding.noOrgTextView.visibility = View.VISIBLE
                binding.rvOrganization.visibility = View.GONE
            } else {
                binding.noOrgTextView.visibility = View.GONE
                binding.rvOrganization.visibility = View.VISIBLE
            }
        }
        viewModel.loadData(requireContext())
    }

    override fun onDestroyView() {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onDestroyView()
        _binding = null
    }

    private fun showJoinOrgDialog() {
        val customDialog = Dialog(requireContext())
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom_2edit, null)
        customDialog.setContentView(dialogView)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val orgIdEditText = dialogView.findViewById<EditText>(R.id.dialog_input_1)
        val inviteEditText = dialogView.findViewById<EditText>(R.id.dialog_input_2)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)
        val okButton = dialogView.findViewById<Button>(R.id.dialog_ok)

        titleTextView.text = "加入新组织"
        inviteEditText.hint = "请输入邀请码"
        orgIdEditText.hint = "请输入组织ID"
        // 设置输入过滤，邀请码仅允许字母
        inviteEditText.filters = arrayOf(InputFilterUtils.onlyLetters())
        cancelButton.setOnClickListener {
            customDialog.dismiss()
        }

        // 仅数字允许
        orgIdEditText.filters = arrayOf(InputFilterUtils.onlyNumbers())
        cancelButton.setOnClickListener {
            customDialog.dismiss()
        }

        okButton.setOnClickListener {
            val inviteCode = inviteEditText.text.toString()
            val orgId = orgIdEditText.text.toString()
            Timber.d("组织ID：${orgId}，邀请码：${inviteCode}")
            if (inviteCode == "") {
                Toast.makeText(requireContext(), "无效邀请码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (orgId == "") {
                Toast.makeText(requireContext(), "无效组织ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userInfo = SharedPreferencesUtil.getUserInfo(requireContext())
            if (userInfo == null) {
                Toast.makeText(requireContext(), "用户信息获取失败", Toast.LENGTH_SHORT).show()
                customDialog.dismiss()
                return@setOnClickListener
            }
            val orgInfo: OrganizationBaseInfo = OrganizationBaseInfo(id = orgId.toInt(), inviteCode = inviteCode)
            viewModel.joinOrganization(requireContext(), orgId.toInt(), orgInfo)
            Toast.makeText(requireContext(), "组织ID：${orgId}，邀请码：${inviteCode}", Toast.LENGTH_SHORT).show()
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun showCreateOrgDialog() {
        val customDialog = Dialog(requireContext())
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_org, null)
        customDialog.setContentView(dialogView)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val descName = dialogView.findViewById<TextView>(R.id.dialog_desc_name)
        val inputName = dialogView.findViewById<EditText>(R.id.dialog_input_name)
        val descType = dialogView.findViewById<TextView>(R.id.dialog_desc_type)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)
        val okButton = dialogView.findViewById<Button>(R.id.dialog_ok)

        titleTextView.text = "创建组织"
        descName.text = "组织名"
        descType.text = "组织类型"
        inputName.hint = "输入组织名称"
        // 设置输入过滤，邀请码仅允许字母
        cancelButton.setOnClickListener {
            customDialog.dismiss()
        }

        // 获取 Spinner 组件实例
        val spinner = dialogView.findViewById<Spinner>(R.id.dialog_spinner_type)

        // 获取 string-array 对应的数组
        val organizationTypes = resources.getStringArray(R.array.organization_types_zh)
        val organizationTypesRow = resources.getStringArray(R.array.organization_types_row)

        // 创建 ArrayAdapter 并设置数据源
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            organizationTypes)

        // 设置下拉列表的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 将适配器设置给 Spinner
        spinner.adapter = adapter

        okButton.setOnClickListener {
            val name = inputName.text.toString()
            val typePosition = spinner.selectedItemPosition
            val type = organizationTypesRow[typePosition]
            Timber.d("组织名：${name}，组织类型：${type}")
            if (name == "" || type == "") {
                Toast.makeText(requireContext(), "输入错误", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "组织名：${name}，组织类型：${type}", Toast.LENGTH_SHORT).show()

            // 创建组织
            val orgInfo = OrganizationBaseInfo(0, name, type, "")
            viewModel.createOrganization(requireContext(), orgInfo)
            customDialog.dismiss()
        }

        customDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
    }

    override fun onItemClick(orgInfo: OrganizationBaseInfo) {
        // 创建 Bundle 用于传递数据
        val bundle = Bundle()
        bundle.putString("id", orgInfo.id.toString())
        bundle.putString("name", orgInfo.name)
        bundle.putString("type", orgInfo.type)
        bundle.putString("inviteCode", orgInfo.inviteCode)

        findNavController().navigate(R.id.action_nav_organization_to_nav_org_detail, bundle)
    }
}