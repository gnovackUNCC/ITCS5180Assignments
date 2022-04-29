package com.gnovack.novack_midterm

import android.content.Context
import android.os.Bundle
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
class CategoryFragment : Fragment(), CategoryRecyclerViewAdapter.CategoryClickListener {

    private var columnCount = 1
    private val categories = DataServices.getCategories()

    lateinit var categoryActions: CategoryActions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = CategoryRecyclerViewAdapter(categories, this@CategoryFragment)
            }
        }
        activity?.title = getString(R.string.pick_category)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoryActions = context as CategoryActions
    }

    override fun onClick(category: String) {
        categoryActions.categorySelected(category)
    }
    interface CategoryActions {
        fun categorySelected(category: String)
    }
}