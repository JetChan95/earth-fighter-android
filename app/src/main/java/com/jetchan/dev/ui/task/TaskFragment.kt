package com.jetchan.dev.ui.task

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
import com.jetchan.dev.databinding.FragmentTaskBinding
import com.jetchan.dev.src.task.TaskAdapter
import com.jetchan.dev.utils.getCurrentFunctionName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private var dataLoaded = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        val taskViewModel =
            ViewModelProvider(this).get(TaskViewModel::class.java)

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.rv_task)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskAdapter(emptyArray())
        recyclerView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onViewCreated(view, savedInstanceState)
        if (!dataLoaded) {
            loadData()
        }
    }

    override fun onResume() {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onResume()
        if (adapter.itemCount == 0) {
            loadData()
        }
    }

    private fun loadData() {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                // 加载数据
                Thread.sleep(200)
                Array<String>(100) { index ->
                    "Task${index + 1}"
                }
            }
            adapter.updateData(data)
            dataLoaded = true
        }
    }

    override fun onDestroyView() {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onDestroy()
        _binding = null
    }
}