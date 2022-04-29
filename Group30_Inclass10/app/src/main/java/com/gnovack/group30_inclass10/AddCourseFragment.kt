package com.gnovack.group30_inclass10

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gnovack.group30_inclass10.databinding.FragmentAddCourseBinding

//Inclass 10
//AddCourseFragment
//Gabriel Novack

class AddCourseFragment : Fragment() {
    lateinit var binding: FragmentAddCourseBinding
    lateinit var addListener: AddListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCourseBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.add_course)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButton.setOnClickListener {
            if (binding.editCode.text.isEmpty() || binding.editName.text.isEmpty() || binding.editHours.text.isEmpty()) {
                Toast.makeText(activity, "No Field should be empty!", Toast.LENGTH_SHORT).show()
            } else {
                val gradeGroup = binding.gradeGroup
                val checkedGrade =
                    binding.root.findViewById(gradeGroup.checkedRadioButtonId) as RadioButton
                addListener.onAddSubmit(
                    Course(
                        binding.editCode.text.toString(),
                        binding.editName.text.toString(),
                        binding.editHours.text.toString().toInt(),
                        checkedGrade.text.toString()
                    )
                )
            }
        }

        binding.cancelButton.setOnClickListener {
            addListener.onCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addListener = context as AddListener
    }

    interface AddListener {
        fun onAddSubmit(course: Course)
        fun onCancel()
    }
}