package com.jetchan.dev.ui.home

import android.os.Bundle
import android.os.Debug
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentHomeBinding
import com.jetchan.dev.src.MyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.fragment.findNavController
import com.jetchan.dev.utils.NavStackTracker
import com.jetchan.dev.utils.getCurrentFunctionName
import timber.log.Timber

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private var dataLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        NavStackTracker(navController, "home")
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.rv_users)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyAdapter(emptyArray())
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
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        if (adapter.itemCount == 0) {
            loadData()
        }
    }

    private fun loadData() {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                // 模拟耗时的数据加载操作
                Thread.sleep(200)
                arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
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