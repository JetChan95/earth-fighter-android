package com.jetchan.dev.ui.organization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentOrganizationBinding
import com.jetchan.dev.src.OrganizationAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrganizationFragment : Fragment() {

    private var _binding: FragmentOrganizationBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrganizationAdapter
    private var dataLoaded = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val organizationViewModel =
            ViewModelProvider(this).get(OrganizationViewModel::class.java)

        _binding = FragmentOrganizationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = root.findViewById(R.id.rv_organization)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrganizationAdapter(emptyArray())
        recyclerView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!dataLoaded) {
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter.itemCount == 0) {
            loadData()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                // 模拟耗时的数据加载操作
                Thread.sleep(200)
                Array<String>(100) { index ->
                    "Organization${index + 1}"
                }
            }
            adapter.updateData(data)
            dataLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}