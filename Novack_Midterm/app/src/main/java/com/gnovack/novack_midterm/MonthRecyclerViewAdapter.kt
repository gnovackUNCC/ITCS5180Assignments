package com.gnovack.novack_midterm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.novack_midterm.databinding.FragmentMonthBinding
import java.util.*

class MonthRecyclerViewAdapter(
    private val values: SortedMap<Pair<Int,Int>, Double>
) : RecyclerView.Adapter<MonthRecyclerViewAdapter.ViewHolder>() {

    private val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentMonthBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keyArr = values.keys.toTypedArray()
        val curKey = keyArr[position]
        holder.monthView.text = holder.itemView.context.resources.getString(R.string.month_year, months[curKey.first], curKey.second + 1900)
        holder.amountView.text = holder.itemView.context.resources.getString(R.string.num_amount, values[curKey])
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentMonthBinding) : RecyclerView.ViewHolder(binding.root) {
        val monthView = binding.month
        val amountView = binding.monthAmount

    }

}