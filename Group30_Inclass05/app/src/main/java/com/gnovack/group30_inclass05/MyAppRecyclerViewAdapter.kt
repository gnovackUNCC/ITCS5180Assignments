package com.gnovack.group30_inclass05

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.group30_inclass05.databinding.FragmentAppBinding

//Gabriel Novack
//AppRecyclerViewAdapter
//In Class 05

class MyAppRecyclerViewAdapter(
    private val values: List<DataServices.App>,
    private val appClickListener: AppClickListener
) : RecyclerView.Adapter<MyAppRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            appClickListener
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.appName.text = item.name
        holder.artistName.text = item.artistName
        holder.releaseDate.text = item.releaseDate
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAppBinding, appClickListener: AppClickListener) : RecyclerView.ViewHolder(binding.root) {
        val artistName = binding.artistName
        val appName = binding.appName
        val releaseDate = binding.releaseDate

        init {
            binding.root.setOnClickListener {
                val selectedApp = values.find { item -> item.name == binding.appName.text.toString() }
                appClickListener.appClick(selectedApp)
            }
        }
    }

    interface AppClickListener {
        fun appClick(appSelected:DataServices.App?)
    }

}