package com.gnovack.group30_inclass10

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Inclass 10
//Course
//Gabriel Novack

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey @ColumnInfo(name = "course_code") val courseCode: String,
    @ColumnInfo(name = "course_name") val courseName: String,
    @ColumnInfo(name = "course_hours") val courseHours: Int,
    @ColumnInfo(name = "course_grade") val courseGrade: String
)