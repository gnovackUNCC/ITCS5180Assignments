package com.gnovack.novack_midterm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A fragment representing a list of Items.
 */
class MonthFragment : Fragment() {

    private var columnCount = 1
    private var expenseList:ArrayList<Expense>? = null

    private val monthSums = mutableMapOf<Pair<Int,Int>, Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            Log.d("HELLO", "onCreate: arguments")
            expenseList = it.getParcelableArrayList(LIST_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_month_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                expenseList!!.forEach {
                    if(monthSums.contains(Pair(it.date.month, it.date.year)))
                        monthSums[Pair(it.date.month, it.date.year)] = monthSums[Pair(it.date.month, it.date.year)]!! + it.amount
                    else
                        monthSums[Pair(it.date.month, it.date.year)] = it.amount
                }

                adapter = MonthRecyclerViewAdapter(monthSums.toSortedMap(compareBy({-it.second}, {-it.first})))
            }
        }
        activity?.title = getString(R.string.expenses_summary)
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val LIST_KEY = "new_list"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(newList: ArrayList<Expense>) =
            MonthFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(LIST_KEY, newList)
                }
            }
    }
}