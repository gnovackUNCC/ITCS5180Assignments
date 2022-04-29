package com.gnovack.group30_inclass10

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.gnovack.group30_inclass10.databinding.FragmentCourseListBinding

//Inclass 10
//CourseFragment
//Gabriel Novack

class CourseFragment : Fragment(), CourseRecyclerViewAdapter.CourseClickListener {
    lateinit var db: AppDatabase
    lateinit var courseDao: CourseDao
    lateinit var binding: FragmentCourseListBinding
    lateinit var courseListActions: CourseListActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "course-database").allowMainThreadQueries().fallbackToDestructiveMigration().build()
        courseDao = db.courseDao()

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseListBinding.inflate(inflater, container, false)

        // Set the adapter
        with(binding.courseList) {
            layoutManager = LinearLayoutManager(context)
        }
        activity?.title = getString(R.string.grades)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.course_add){
            courseListActions.onAddClicked()
            true
        }else{
            super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseList = courseDao.getAllUsers()
        updateList(courseList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        courseListActions = context as CourseListActions
    }

    override fun onResume() {
        super.onResume()
        val courseList = courseDao.getAllUsers()
        updateList(courseList)
    }

    fun updateList(courseList:List<Course>){
        var gpaSum = 0.0
        var hourSum = 0.0
        for(course in courseList){
            gpaSum += (when(course.courseGrade){
                "A" -> 4
                "B" -> 3
                "C" -> 2
                "D" -> 1
                "F" -> 0
                else -> 0
            }) * course.courseHours
            hourSum += course.courseHours
        }
        binding.totalGpa.text = getString(R.string.total_gpa, when{
            hourSum>0 -> gpaSum / hourSum
            else -> 4.0
        })
        binding.totalHours.text = getString(R.string.total_hours, hourSum)
        with(binding.courseList) {
            adapter = CourseRecyclerViewAdapter(courseList, this@CourseFragment)
        }
    }

    override fun onDelete(course: Course) {
        courseDao.deleteCourse(course)
        val courseList = courseDao.getAllUsers()
        updateList(courseList)
    }

    interface CourseListActions {
        fun onAddClicked()
    }
}