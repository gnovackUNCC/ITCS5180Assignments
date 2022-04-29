package com.gnovack.group30_inclass10

import androidx.room.Database
import androidx.room.RoomDatabase

//Inclass 10
//AppDatabase
//Gabriel Novack

@Database(entities = [Course::class], version=2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun courseDao():CourseDao
}