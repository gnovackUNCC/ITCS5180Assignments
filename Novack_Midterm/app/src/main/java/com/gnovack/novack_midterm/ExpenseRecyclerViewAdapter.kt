package com.gnovack.novack_midterm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_midterm.databinding.FragmentExpenseBinding


class ExpenseRecyclerViewAdapter(
    private val values: ArrayList<Expense>,
    private val trashClickListener: TrashClickListener
) : RecyclerView.Adapter<ExpenseRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentExpenseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.amountView.text = holder.itemView.context.resources.getString(R.string.num_amount, item.amount)
        holder.catView.text = "(${item.category})"
        holder.dateView.text = MainActivity().simpleDateFormat.format(item.date)
        holder.trashButton.setOnClickListener {
            trashClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView = binding.expName
        val amountView = binding.expAmount
        val dateView = binding.expDate
        val catView = binding.expCat
        val trashButton = binding.expTrash
    }

    interface TrashClickListener {
        fun onClick(position:Int)
    }

}