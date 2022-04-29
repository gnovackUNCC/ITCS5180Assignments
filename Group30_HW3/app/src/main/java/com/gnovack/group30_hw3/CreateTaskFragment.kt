package com.gnovack.group30_hw3

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gnovack.group30_hw3.databinding.FragmentCreateTaskBinding

//HW03
//CreateTaskFragment
//Gabriel Novack

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTaskFragment : Fragment() {

    lateinit var binding: FragmentCreateTaskBinding

    lateinit var createActions: CreateActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.create_task)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            createActions.onCreateCancel()
        }
        binding.submitButton.setOnClickListener {
            if(binding.taskEditName.text.isEmpty() || binding.showSetDate.text.isEmpty()){
                Toast.makeText(activity, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
            }
            else {
                val name = binding.taskEditName.text.toString()
                val date =
                    MainActivity().simpleDateFormat.parse(binding.showSetDate.text.toString())
                val priority =
                    view.findViewById<RadioButton>(binding.priorityGroup.checkedRadioButtonId).text.toString()
                createActions.onTaskSubmitted(Task(name, date!!, priority))
            }
        }
        binding.setDate.setOnClickListener {
            val dateDialog: DatePickerDialog = DatePickerDialog(requireContext())
            dateDialog.setOnDateSetListener{ datePicker: DatePicker, y: Int, m: Int, d: Int ->
                binding.showSetDate.text = "${m+1}/$d/$y"
            }
            dateDialog.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createActions = context as CreateActions
    }

    interface CreateActions {
        fun onTaskSubmitted(task: Task)
        fun onCreateCancel()
    }
}