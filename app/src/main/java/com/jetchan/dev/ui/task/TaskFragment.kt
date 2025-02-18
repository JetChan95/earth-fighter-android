package com.jetchan.dev.ui.task

import TaskViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentTaskBinding
import com.jetchan.dev.src.task.TaskAdapter
import com.jetchan.dev.utils.getCurrentFunctionName
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var noTasksTextView: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by lazy {
        ViewModelProvider(this)[TaskViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.rv_task)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskAdapter(arrayListOf())
        recyclerView.adapter = adapter

        noTasksTextView = root.findViewById(R.id.no_tasks_text_view)
//        noTasksTextView.visibility = View.GONE

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("Trace life time ${this::class.simpleName}.${getCurrentFunctionName()}")
        super.onViewCreated(view, savedInstanceState)

        // 观察 LiveData 的变化
        viewModel.taskList.observe(viewLifecycleOwner) { tasks ->
            adapter.updateData(tasks)
            if (tasks.isEmpty()) {
                noTasksTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                Timber.d("show no tasks")
            } else {
                noTasksTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                Timber.d("show task list")
            }
        }

        // 加载数据
        lifecycleScope.launch {
            viewModel.loadTasks(requireContext())
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