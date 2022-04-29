package com.gnovack.novack_hw7

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_hw7.databinding.FragmentShoppingItemBinding

class ShoppingItemRecyclerViewAdapter(
    private val values: List<ShoppingItem>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<ShoppingItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentShoppingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.itemName.text = item.name
        holder.itemLayout.setOnClickListener {
            itemClickListener.onItemChecked(position)
        }

        if(item.checked){
            holder.itemLayout.foreground = holder.itemView.context.getDrawable(R.drawable.strikethrough)
            holder.itemLayout.alpha = 0.5F
        }
        holder.itemDelete.setOnClickListener {
            itemClickListener.onDelete(item)
        }

        holder.itemCost.text = item.cost

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemName = binding.itemName
        val itemDelete = binding.itemDelete
        val itemLayout = binding.itemLayout
        val itemCost = binding.itemCost
    }

    interface ItemClickListener {
        fun onDelete(item:ShoppingItem)
        fun onItemChecked(position:Int)
    }

}