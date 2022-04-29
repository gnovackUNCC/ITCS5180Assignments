package com.gnovack.group30_inclass05

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//Gabriel Novack
//CategoryFragment
//In Class 05

/**
 * A fragment representing a list of Items.
 */
class CategoryFragment : Fragment(), MyCategoryRecyclerViewAdapter.CategoryClickListener {

    private var columnCount = 1

    val categories = DataServices.getAppCategories()

    lateinit var categoryActions: CategoryActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.app_list)

        // Set the adapter
        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyCategoryRecyclerViewAdapter(categories, this@CategoryFragment)
        }
        activity?.title = getString(R.string.app_categories)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoryActions = context as CategoryActions
    }

    override fun onClick(categorySelected: String) {
        Log.d("CLICK", "onClick: $categorySelected")
        categoryActions.categoryClick(categorySelected)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
    interface CategoryActions {
        fun categoryClick(categorySelected: String)
    }
}