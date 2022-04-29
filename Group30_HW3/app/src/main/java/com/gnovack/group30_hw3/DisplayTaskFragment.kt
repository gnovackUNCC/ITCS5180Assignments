package com.gnovack.group30_hw3

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gnovack.group30_hw3.databinding.FragmentDisplayTaskBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

//HW03
//DisplayTaskFragment
//Gabriel Novack

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DisplayTaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var curTask:Task? = null

    lateinit var binding: FragmentDisplayTaskBinding

    lateinit var displayActions: DisplayActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            curTask = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDisplayTaskBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.display_task)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showName.text = curTask?.name
        binding.showDate.text = MainActivity().simpleDateFormat.format(curTask?.date)
        binding.showPriority.text = curTask?.priority

        binding.displayDelete.setOnClickListener {
            displayActions.onDelete(curTask!!)
        }
        binding.displayCancel.setOnClickListener {
            displayActions.onCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        displayActions = context as DisplayActions
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DisplayTaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Task) =
            DisplayTaskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }

    interface DisplayActions{
        fun onDelete(task: Task)
        fun onCancel()
    }
}