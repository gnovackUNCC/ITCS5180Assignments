package com.gnovack.novack_midterm

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_midterm.databinding.FragmentCategoryBinding


class CategoryRecyclerViewAdapter(
    private val values: List<String>,
    private val categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>() {

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
        val contentView: TextView = binding.category
        init {
            binding.root.setOnClickListener {
                categoryClickListener.onClick(binding.category.text.toString())
            }
        }
    }
    interface CategoryClickListener {
        fun onClick(category:String)
    }

}