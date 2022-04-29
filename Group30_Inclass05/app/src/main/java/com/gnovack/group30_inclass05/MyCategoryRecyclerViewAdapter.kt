package com.gnovack.group30_inclass05

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.gnovack.group30_inclass05.databinding.FragmentCategoryBinding

//Gabriel Novack
//CategoryRecyclerViewAdapter
//In Class 05

class MyCategoryRecyclerViewAdapter(
    private val values: List<String>,
    private val categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<MyCategoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            categoryClickListener
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView.text = item
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentCategoryBinding, categoryClickListener: CategoryClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.catTitle

        init {
            binding.root.setOnClickListener {
                categoryClickListener.onClick(binding.catTitle.text.toString())
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    interface CategoryClickListener {
        fun onClick(categorySelected:String)
    }

}