package com.gnovack.group30_inclass06

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.group30_inclass06.databinding.NumberListItemBinding

//Inclass06
//NumberListAdapter
//Gabriel Novack

class NumberListAdapter(
    private val values:List<Double>
): RecyclerView.Adapter<NumberListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NumberListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.numText.text = item.toString()
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(binding: NumberListItemBinding): RecyclerView.ViewHolder(binding.root) {
        val numText = binding.numText
    }

}