package com.gnovack.novack_midterm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnovack.novack_midterm.databinding.FragmentExpenseListBinding

/**
 * A fragment representing a list of Items.
 */
class ExpenseFragment : Fragment(), ExpenseRecyclerViewAdapter.TrashClickListener {

    private var columnCount = 1
    private var curList:ArrayList<Expense>? = null

    lateinit var binding:FragmentExpenseListBinding
    lateinit var expenseActions: ExpenseActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HELLO", "onCreate: create")
        arguments?.let {
            Log.d("HELLO", "onCreate: arguments")
            curList = it.getParcelableArrayList(LIST_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false)

        // Set the adapter
        with(binding.expList) {
            layoutManager = LinearLayoutManager(context)
            adapter = ExpenseRecyclerViewAdapter(curList!!, this@ExpenseFragment)
        }
        activity?.title = getString(R.string.expenses)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addExp.setOnClickListener {
            expenseActions.addExpense()
        }
        binding.expSummary.setOnClickListener {
            expenseActions.viewSummary()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        expenseActions = context as ExpenseActions
    }

    override fun onResume() {
        super.onResume()
        expenseActions.updateExpenses(this)
    }

    override fun onClick(position: Int) {
        expenseActions.expTrashed(this, position)
    }

    fun updateList(updatedList:ArrayList<Expense>){
        binding.numExp.text = curList!!.size.toString()
        var sum = 0.0
        curList!!.forEach {
            sum += it.amount
        }
        binding.totalExp.text = resources.getString(R.string.num_amount, sum)
        with(binding.expList) {
            adapter = ExpenseRecyclerViewAdapter(updatedList, this@ExpenseFragment)
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val LIST_KEY = "new_list"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(newList: ArrayList<Expense>) =
            ExpenseFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(LIST_KEY, newList)
                }
            }
    }

    interface ExpenseActions {
        fun addExpense()
        fun expTrashed(fragment: ExpenseFragment, position: Int)
        fun updateExpenses(fragment: ExpenseFragment)
        fun viewSummary()
    }
}