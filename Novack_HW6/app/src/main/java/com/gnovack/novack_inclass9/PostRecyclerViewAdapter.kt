package com.gnovack.novack_inclass9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_inclass9.databinding.FragmentPostBinding

//HW6
//PostRecyclerViewAdapter
//Gabriel Novack
class PostRecyclerViewAdapter(
    private val values: List<Forum>,
    private val postClickListener: PostClickListener,
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
                postClickListener.onDelete(item.postId!!)
            }
        }
        if(item.likedByCurrent!!)
            holder.postLike.setImageDrawable(holder.postLike.rootView.context.getDrawable(R.drawable.like_favorite))
        holder.postLike.setOnClickListener {
            if(holder.postLike.drawable.constantState == holder.postLike.rootView.context.getDrawable(R.drawable.like_not_favorite)!!.constantState){
                holder.postLike.setImageDrawable(holder.postLike.rootView.context.getDrawable(R.drawable.like_favorite))
                postClickListener.onLikeClick(item.postId!!, true)
            } else {
                holder.postLike.setImageDrawable(holder.postLike.rootView.context.getDrawable(R.drawable.like_not_favorite))
                postClickListener.onLikeClick(item.postId!!, false)
            }
        }

        holder.likeNum.text = when(item.likes.size){
            1 -> holder.postLike.rootView.context.getString(R.string.like_num, item.likes.size)
            else -> holder.postLike.rootView.context.getString(R.string.like_num_pl, item.likes.size)
        }

        holder.postLayout.setOnClickListener {
            postClickListener.onPostClick(item.postId!!)
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val postContents = binding.postContent
        val postCreator = binding.postCreator
        val postTime = binding.postTime
        val postTitle = binding.postTitle
        val postDelete = binding.postDelete
        val postLike = binding.postLike
        val likeNum = binding.postLikeNum
        val postLayout = binding.postLayout
    }

    interface PostClickListener {
        fun onDelete(postId:String)
        fun onLikeClick(postId:String, curLiked:Boolean)
        fun onPostClick(postId:String)
    }

}