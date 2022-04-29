package com.gnovack.group30_inclass05

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.gnovack.group30_inclass05.databinding.FragmentGenreBinding

//Gabriel Novack
//GenreRecyclerViewAdapter
//In Class 05

class MyGenreRecyclerViewAdapter(
    private val curApp: DataServices.App
) : RecyclerView.Adapter<MyGenreRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentGenreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = curApp.genres[position]
        holder.genreName.text = item
    }

    override fun getItemCount(): Int = curApp.genres.size

    inner class ViewHolder(binding: FragmentGenreBinding) : RecyclerView.ViewHolder(binding.root) {
        val genreName = binding.genreTitle
    }

}