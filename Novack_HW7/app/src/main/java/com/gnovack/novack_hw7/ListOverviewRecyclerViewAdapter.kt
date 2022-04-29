package com.gnovack.novack_hw7

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_hw7.databinding.FragmentSummaryBinding

class ListOverviewRecyclerViewAdapter(
    private val values: List<ShoppingList>,
    private val listClickListener: ListClickListener
) : RecyclerView.Adapter<ListOverviewRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.listName.text = item.name
        holder.listOwner.text = item.owner!!

        if(item.createdByCurrent!!){
            holder.listDelete.visibility = View.VISIBLE
            holder.listDelete.setOnClickListener {
                listClickListener.onDelete(item.id!!)
            }
        }

        holder.listLayout.setOnClickListener {
            listClickListener.onListSelected(item.id!!)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val listName = binding.listName
        val listOwner = binding.listOwner
        val listDelete = binding.listDelete
        val listLayout = binding.summaryLayout

    }

    interface ListClickListener {
        fun onDelete(listId:String)
        fun onListSelected(listId:String)
    }

}