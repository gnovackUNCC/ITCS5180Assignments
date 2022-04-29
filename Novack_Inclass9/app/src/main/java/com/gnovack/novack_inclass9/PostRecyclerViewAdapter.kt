package com.gnovack.novack_inclass9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_inclass9.databinding.FragmentPostBinding

//HW5
//PostRecyclerViewAdapter
//Gabriel Novack
class PostRecyclerViewAdapter(
    private val values: List<Forum>,
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
        holder.postTitle.text = item.title
        holder.postContents.text = item.desc
        val date = MainActivity().dateFormat.format(item.timeCreated.toDate())
        holder.postTime.text = date
        holder.postCreator.text = item.createdBy
        if(item.createdByCurrent!!){
            holder.postDelete.visibility = View.VISIBLE
            holder.postDelete.setOnClickListener {
                deleteClickListener.onDelete(item.postId!!)
            }
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val postContents = binding.postContent
        val postCreator = binding.postCreator
        val postTime = binding.postTime
        val postTitle = binding.postTitle
        val postDelete = binding.postDelete
    }

    interface DeleteClickListener {
        fun onDelete(postId:String)
    }

}