package com.gnovack.hw03

import java.io.Serializable
import java.util.*

//HW02
//Tasks
//Gabriel Novack

data class Tasks(val name:String, val date:Date, val priority:String):Serializable {
    override fun toString(): String {
        return name
    }
}