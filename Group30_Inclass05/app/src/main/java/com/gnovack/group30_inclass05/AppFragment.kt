package com.gnovack.group30_inclass05

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

//Gabriel Novack
//AppFragment
//In Class 05

/**
 * A fragment representing a list of Items.
 */
class AppFragment : Fragment(), MyAppRecyclerViewAdapter.AppClickListener {

    private var columnCount = 1
    private var catType:String? = null

    lateinit var appActions: AppActions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            catType = it.getString(CAT_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.app_list)

        // Set the adapter
        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyAppRecyclerViewAdapter(DataServices.getAppsByCategory(catType), this@AppFragment)
        }
        activity?.title = catType
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appActions = context as AppActions
    }

    override fun appClick(appSelected: DataServices.App?) {
        Log.d("CLICK", "appClick: ${appSelected!!.name}")
        appActions.appClick(appSelected)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val CAT_TYPE = "cat-type"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int, catType:String) =
            AppFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putString(CAT_TYPE, catType)
                }
            }
    }

    interface AppActions {
        fun appClick(appSelected: DataServices.App?)
    }
}