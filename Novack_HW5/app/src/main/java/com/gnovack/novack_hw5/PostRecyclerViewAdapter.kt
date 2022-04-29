package com.gnovack.novack_hw5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_hw5.databinding.FragmentPostBinding

//HW5
//PostRecyclerViewAdapter
//Gabriel Novack

class PostRecyclerViewAdapter(
    private val values: List<Post>,
    private val deleteClickListener: DeleteClickListener,
) : RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.postTitle.text = item.text
        holder.postName.text = item.createdBy
        val fullTime = item.createdAt
        val timeParts = fullTime.split(" ")
        holder.postTime.text = holder.itemView.context.resources.getString(R.string.post_time, timeParts[0], timeParts[1])
        if(item.createdByUser){
            holder.postDelete.visibility = View.VISIBLE
            holder.postDelete.setOnClickListener {
                deleteClickListener.onDelete(item.postId)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val postName = binding.postName
        val postTime = binding.postTime
        val postTitle = binding.postTitle
        val postDelete = binding.postDelete
    }

    interface DeleteClickListener {
        fun onDelete(postId:String)
    }

}