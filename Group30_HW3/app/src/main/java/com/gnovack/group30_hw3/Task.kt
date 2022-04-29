package com.gnovack.group30_hw3

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

//HW02
//Task
//Gabriel Novack

@Parcelize
data class Task(val name:String, val date: Date, val priority:String): Parcelable {
    override fun toString(): String {
        return name
    }
}