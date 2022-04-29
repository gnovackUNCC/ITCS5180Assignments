package com.gnovack.novack_inclass9

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gnovack.novack_inclass9.databinding.FragmentCommentBinding

//HW6
//CommentRecyclerViewAdapter
//Gabriel Novack
class CommentRecyclerViewAdapter(
    private val values: List<Comment>,
    private val commentClickListener: CommentClickListener
) : RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.commentTitle.text = item.createdBy
        holder.commentContent.text = item.content
        val date = MainActivity().dateFormat.format(item.timeCreated.toDate())
        holder.commentTime.text = date
        if(item.createdByCurrent!!){
            holder.commentDelete.visibility = View.VISIBLE
            holder.commentDelete.setOnClickListener {
                commentClickListener.onDelete(item.commentId!!)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val commentTitle = binding.commentName
        val commentContent = binding.commentContent
        val commentTime = binding.commentTime
        val commentDelete = binding.commentDelete
    }

    interface CommentClickListener {
        fun onDelete(commentId:String)
    }
}