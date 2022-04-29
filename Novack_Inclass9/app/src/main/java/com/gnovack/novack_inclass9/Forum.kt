package com.gnovack.novack_inclass9

import com.google.firebase.Timestamp
//HW5
//Forum
//Gabriel Novack
data class Forum(var createdBy:String, val title:String, val desc:String, val timeCreated:Timestamp, val postId:String? = null, var createdByCurrent: Boolean? = false)