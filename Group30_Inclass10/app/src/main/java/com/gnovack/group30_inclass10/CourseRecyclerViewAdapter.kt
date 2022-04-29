package com.gnovack.group30_inclass10

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gnovack.group30_inclass10.databinding.FragmentCourseBinding

//Inclass 10
//CourseRecyclerViewAdapter
//Gabriel Novack

class CourseRecyclerViewAdapter(
    private val values: List<Course>,
    private val courseClickListener: CourseClickListener
) : RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.courseCode.text = item.courseCode
        holder.courseName.text = item.courseName
        holder.courseGrade.text = item.courseGrade
        holder.courseHours.text = when(item.courseHours) {
            1 -> holder.itemView.context.getString(R.string.course_hours, item.courseHours)
            else -> holder.itemView.context.getString(R.string.course_hours_pl, item.courseHours)
        }
        holder.courseDelete.setOnClickListener {
            courseClickListener.onDelete(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        val courseCode = binding.courseCode
        val courseName = binding.courseName
        val courseHours = binding.courseHours
        val courseGrade = binding.courseGrade
        val courseDelete = binding.courseDelete
    }

    interface CourseClickListener {
        fun onDelete(course:Course)
    }
}