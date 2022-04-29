package com.gnovack.group30_inclass05

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnovack.group30_inclass05.databinding.FragmentGenreListBinding

//Gabriel Novack
//GenreFragment
//In Class 05

/**
 * A fragment representing a list of Items.
 */
class GenreFragment : Fragment() {

    private var columnCount = 1
    private var curApp:DataServices.App? = null

    lateinit var binding: FragmentGenreListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            curApp = it.getSerializable(SELECTED_APP) as DataServices.App
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenreListBinding.inflate(inflater, container, false)

        // Set the adapter
        with(binding.genreList) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyGenreRecyclerViewAdapter(curApp!!)
        }
        activity?.title = getString(R.string.app_details)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detAppName.text = curApp?.name ?:"none"
        binding.detArtistName.text = curApp?.artistName ?:"none"
        binding.detReleaseDate.text = curApp?.releaseDate ?:"none"
    }

    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val SELECTED_APP = "selected-app"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int, checkedApp:DataServices.App) =
            GenreFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putSerializable(SELECTED_APP, checkedApp)
                }
            }
    }
}