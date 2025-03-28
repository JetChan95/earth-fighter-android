package com.jetchan.dev.ui.organization

import PublishTaskViewModel
import TodoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentPublishTaskBinding
import com.jetchan.dev.src.TaskResponse
import com.jetchan.dev.src.task.TaskCallback

class PublishTaskFragment :Fragment(){
    private var _binding: FragmentPublishTaskBinding? = null
    private lateinit var todoAdapter: TodoAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PublishTaskViewModel by lazy {
        ViewModelProvider(this)[PublishTaskViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublishTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化任务类型下拉选项
        val taskTypes = resources.getStringArray(R.array.task_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taskTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTaskType.adapter = adapter

        val id = arguments?.getInt("id")
        id?.let { viewModel.setOrgId(it) }

        // 初始化待办项目列表
        todoAdapter = TodoAdapter(mutableListOf())
        binding.rvTodoItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodoItems.adapter = todoAdapter

        // 观察待办事项列表的变化
        viewModel.todoList.observe(viewLifecycleOwner) {
            todoAdapter.updateData(it)
        }

        // 添加待办项目按钮点击事件
        binding.btnAddTodo.setOnClickListener {
            val todoText = binding.etTodoItem.text.toString().trim()
            if (todoText.isNotEmpty()) {
                viewModel.addTodoItem(Pair(todoText, false))
                binding.etTodoItem.text.clear()
            } else {
                Toast.makeText(requireContext(), "请输入待办项目", Toast.LENGTH_SHORT).show()
            }
        }

        // 发布任务按钮点击事件
        binding.btnPublishTask.setOnClickListener {
            val taskName = binding.etTaskName.text.toString().trim()
            val taskType = binding.spTaskType.selectedItem.toString()
            val taskDescription = binding.etTaskDescription.text.toString().trim()

            viewModel.setTaskName(taskName)
            viewModel.setTaskType(taskType)
            viewModel.setTaskDescription(taskDescription)

            viewModel.publishTask(requireContext(), object : TaskCallback {
                override fun onResult(success: Boolean, message: String) {
                    if (success) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}