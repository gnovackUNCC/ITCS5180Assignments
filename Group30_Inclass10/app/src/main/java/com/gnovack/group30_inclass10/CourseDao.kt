package com.gnovack.group30_inclass10

import androidx.room.*

//Inclass 10
//CourseDao
//Gabriel Novack

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllUsers(): List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourses(vararg courses: Course)

    @Delete
    fun deleteCourse(course:Course)
}
