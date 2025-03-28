package com.jetchan.dev.ui.organization

import TodoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.jetchan.dev.R
import com.jetchan.dev.databinding.FragmentOrgTaskBinding
import com.jetchan.dev.src.Task
import com.jetchan.dev.src.TaskStatus
import com.jetchan.dev.src.task.TaskCallback
import com.jetchan.dev.utils.SharedPreferencesUtil
import timber.log.Timber

class OrgTaskDetailFragment: Fragment() {
    private var _binding: FragmentOrgTaskBinding? = null
    private lateinit var todoAdapter: TodoAdapter


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val taskViewModel: OrgTaskDetailViewModel by lazy {
        ViewModelProvider(this)[OrgTaskDetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrgTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化待办项目列表
        todoAdapter = TodoAdapter(mutableListOf())
        binding.rvTodoItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodoItems.adapter = todoAdapter

        val task = arguments?.getString("task")
        task?.let{taskViewModel.setTask(Gson().fromJson(task, Task::class.java))}

        setUpBtn()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 观察待办事项列表的变化
        taskViewModel.todoList.observe(viewLifecycleOwner) { todoList ->
            Timber.d("Todo list updated: ${Gson().toJson(todoList)}")
            todoAdapter.updateData(todoList)
        }

        // 观察 LiveData 的变化
        taskViewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let {
                Timber.d("Task data updated: ${Gson().toJson(task)}")
                updateTaskInfo(task)
                val userId = SharedPreferencesUtil.getUserInfo(requireContext())?.id
                updateBtnStatus(task.state, userId == task.publisherId, userId == task.receiverId)
            }
        }

        // 观察任务描述文本的变化
        taskViewModel.taskDesc.observe(viewLifecycleOwner) { taskDesc ->
            Timber.d("Task description updated: $taskDesc")
            binding.tvTaskDesc.text = taskDesc
        }
    }

    private fun updateTaskInfo(task: Task) {
        binding.tvTaskId.text = task.id.toString() ?: ""
        binding.tvTaskName.text = task.name ?: ""
        binding.tvPublisherId.text = task.publisherId.toString() ?: ""
        binding.tvReceiverId.text = task.receiverId.toString() ?: ""
        binding.tvTaskState.text = task.state ?: ""
        binding.tvCompletionTime.text = task.completionTime ?: ""
        binding.tvOrgId.text = task.orgId.toString() ?: ""
        binding.tvPublishTime.text = task.publishTime ?: ""
        binding.tvTimeLimit.text = task.timeLimit.toString() ?: ""
    }


    private fun updateBtnStatus(taskState: String, isCreator: Boolean, isReceiver: Boolean) {
        // 定义一个映射表，存储不同任务状态对应的按钮可见性设置
        val buttonVisibilityMap = mapOf(
            TaskStatus.PENDING to {
                mutableMapOf(
                    R.id.btn_accept_task to if (!isCreator) View.VISIBLE else View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.IN_PROGRESS to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to if (isReceiver) View.VISIBLE else View.GONE,
                    R.id.btn_submit_task to if (isReceiver) View.VISIBLE else View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.COMPLETED to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.EXPIRED to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to if (isReceiver) View.VISIBLE else View.GONE,
                    R.id.btn_submit_task to if (isReceiver) View.VISIBLE else View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.OVERDUE_COMPLETED to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.FAILED to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.TO_BE_CONFIRMED to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to if (isCreator) View.VISIBLE else View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.ABANDONED to {
                mutableMapOf(
                    R.id.btn_accept_task to if (!isCreator) View.VISIBLE else View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            },
            TaskStatus.DEFAULT to {
                mutableMapOf(
                    R.id.btn_accept_task to View.GONE,
                    R.id.btn_give_up_task to View.GONE,
                    R.id.btn_submit_task to View.GONE,
                    R.id.btn_confirm_task to View.GONE,
                    R.id.btn_delete_task to if (isCreator) View.VISIBLE else View.GONE
                )
            }
        )

        // 根据任务状态从映射表中获取对应的按钮可见性设置
        val visibilitySettings = buttonVisibilityMap[taskState]?.invoke() ?: buttonVisibilityMap["default"]?.invoke()

        // 遍历可见性设置，更新按钮的可见性
        visibilitySettings?.forEach { (buttonId, visibility) ->
            when (buttonId) {
                R.id.btn_accept_task -> binding.contentTaskHandler.btnAcceptTask.visibility = visibility
                R.id.btn_give_up_task -> binding.contentTaskHandler.btnGiveUpTask.visibility = visibility
                R.id.btn_submit_task -> binding.contentTaskHandler.btnSubmitTask.visibility = visibility
                R.id.btn_confirm_task -> binding.contentTaskHandler.btnConfirmTask.visibility = visibility
                R.id.btn_delete_task -> binding.contentTaskHandler.btnDeleteTask.visibility = visibility
            }
        }
    }

    private fun setUpBtn() {
        binding.contentTaskHandler.btnAcceptTask.setOnClickListener() {
            val taskId = taskViewModel.task.value?.id?:-1
            if (taskId == -1) {
                Toast.makeText(requireContext(), "任务ID无效", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            taskViewModel.acceptTask(requireContext(), taskId, object : TaskCallback {
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

        binding.contentTaskHandler.btnSubmitTask.setOnClickListener() {
            val taskId = taskViewModel.task.value?.id?:-1
            if (taskId == -1) {
                Toast.makeText(requireContext(), "任务ID无效", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            taskViewModel.submitTask(requireContext(), taskId, object : TaskCallback {
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

        binding.contentTaskHandler.btnConfirmTask.setOnClickListener() {
            val taskId = taskViewModel.task.value?.id?:-1
            if (taskId == -1) {
                Toast.makeText(requireContext(), "任务ID无效", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            taskViewModel.confirmTask(requireContext(), taskId, object : TaskCallback {
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

        binding.contentTaskHandler.btnDeleteTask.setOnClickListener() {
            val taskId = taskViewModel.task.value?.id?:-1
            if (taskId == -1) {
                Toast.makeText(requireContext(), "任务ID无效", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            taskViewModel.deleteTask(requireContext(), taskId, object : TaskCallback {
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

        binding.contentTaskHandler.btnGiveUpTask.setOnClickListener() {
            val taskId = taskViewModel.task.value?.id?:-1
            if (taskId == -1) {
                Toast.makeText(requireContext(), "任务ID无效", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            taskViewModel.abandonTask(requireContext(), taskId, object : TaskCallback {
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
}