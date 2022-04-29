package com.gnovack.novack_midterm

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gnovack.novack_midterm.databinding.FragmentAddExpenseBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AddExpenseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExpenseFragment : Fragment() {
    lateinit var binding: FragmentAddExpenseBinding
    lateinit var addActions: AddActions

    private var curDate:String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.add_new_expense)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setDate.text = curDate ?:resources.getString(R.string.none)
        binding.addExpSubmit.setOnClickListener {
            if(binding.addExpName.text.toString() == "" || binding.addExpAmount.text.toString() == "" ||
                binding.setDate.text.toString() == resources.getString(R.string.none)
                || binding.setCat.text.toString() == resources.getString(R.string.none)){
                Toast.makeText(activity, "No field should be left blank or as 'None'", Toast.LENGTH_SHORT).show()
            } else {
                val newExpense = Expense(
                    binding.addExpName.text.toString(),
                    binding.addExpAmount.text.toString().toDouble(),
                    MainActivity().simpleDateFormat.parse(binding.setDate.text.toString()),
                    binding.setCat.text.toString()
                )
                addActions.addSubmitted(newExpense)
            }
        }
        binding.addExpCancel.setOnClickListener {
            addActions.addCanceled()
        }
        binding.dateButton.setOnClickListener {
            val dateDialog: DatePickerDialog = DatePickerDialog(requireContext())
            dateDialog.setOnDateSetListener{ datePicker: DatePicker, y: Int, m: Int, d: Int ->
                binding.setDate.text = "${m+1}/$d/$y"
            }
            dateDialog.show()
        }
        binding.catButton.setOnClickListener {
            curDate = binding.setDate.text.toString()
            addActions.selectCat()
        }
    }

    override fun onResume() {
        super.onResume()
        addActions.catUpdated(this)
    }

    fun updateCategory(category:String){
        binding.setCat.text = category
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addActions = context as AddActions
    }

    interface AddActions {
        fun addSubmitted(newExpense:Expense)
        fun addCanceled()
        fun selectCat()
        fun catUpdated(fragment: AddExpenseFragment)
    }
}