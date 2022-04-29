package com.gnovack.group30_inclass10

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

//Inclass 10
//MainActivity
//Gabriel Novack

class MainActivity : AppCompatActivity(), AddCourseFragment.AddListener, CourseFragment.CourseListActions {
    lateinit var db: AppDatabase
    lateinit var courseDao: CourseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "course-database").allowMainThreadQueries().fallbackToDestructiveMigration().build()
        courseDao = db.courseDao()

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, CourseFragment()).commit()
    }


    override fun onAddSubmit(course: Course) {
        courseDao.insertCourses(course)
        supportFragmentManager.popBackStack()
    }

    override fun onCancel() {
        supportFragmentManager.popBackStack()
    }

    override fun onAddClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AddCourseFragment()).addToBackStack(null).commit()
    }
}