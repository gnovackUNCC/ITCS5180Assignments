package com.gnovack.group30_hw3

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.gnovack.group30_hw3.databinding.FragmentTodoListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

//HW03
//TodolistFragment
//Gabriel Novack

/**
 * A simple [Fragment] subclass.
 * Use the [TodoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodoListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var curTasks:ArrayList<Task>? = null

    lateinit var binding: FragmentTodoListBinding

    lateinit var listActions: ListActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            curTasks = it.getParcelableArrayList(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.app_name)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("UPDATE", "onStart")
        binding.taskNum.text = resources.getString(R.string.num_tasks, curTasks?.size)
        if (curTasks?.size == 0){
            binding.taskTitle.text = getString(R.string.none)
            binding.upcomingTask.setOnClickListener {
                Toast.makeText(activity, getString(R.string.none_available), Toast.LENGTH_SHORT).show()
            }
        } else {
            val firstTask: Task? = curTasks?.get(0)
            binding.taskTitle.text = firstTask?.name
            binding.taskDate.text = MainActivity().simpleDateFormat.format(firstTask?.date)
            binding.taskPriority.text = firstTask?.priority

            binding.upcomingTask.setOnClickListener {
                listActions.onViewTask(firstTask!!)
            }
        }

        binding.createTask.setOnClickListener {
            listActions.onCreateTask()
        }

        binding.viewTasks.setOnClickListener {
            if (curTasks!!.size == 0){
                Toast.makeText(activity, getString(R.string.none_available), Toast.LENGTH_SHORT).show()
            }else{
                val alertDialog: AlertDialog? = requireActivity().let {con ->
                    val builder = AlertDialog.Builder(con)
                    builder.setTitle("Select Task")
                        .setItems(curTasks?.map { it.name }?.toTypedArray(), DialogInterface.OnClickListener { dialog, which ->
                            listActions.onViewTask(curTasks!![which])
                        })
                        .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ ->})
                    builder.create()
                }
                alertDialog?.show()
            }
        }
        Log.d("UPDATE", "onStart: Done")
    }

    override fun onResume() {
        Log.d("UPDATE", "onResume")
        super.onResume()
        listActions.onUpdate(this)
        Log.d("UPDATE", "onResume:Done")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listActions = context as ListActions
    }

    fun updateList(newList:ArrayList<Task>){
        Log.d("UPDATE", "updateList")
        curTasks = newList
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TodoListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: ArrayList<Task>) =
            TodoListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PARAM1, param1)
                }
            }
    }

    interface ListActions {
        fun onCreateTask()
        fun onViewTask(task:Task)
        fun onUpdate(todoListFragment: TodoListFragment)
    }
}

