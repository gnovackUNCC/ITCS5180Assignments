package com.gnovack.novack_inclass9

import com.google.firebase.Timestamp
import java.io.Serializable

//HW6
//Forum
//Gabriel Novack
data class Forum(var createdBy:String, val title:String, val desc:String, val timeCreated:Timestamp, val likes: List<String> = listOf(), val postId:String? = null, var createdByCurrent: Boolean? = false, var likedByCurrent: Boolean? = false, var comments: List<Comment> = listOf()): Serializable
data class Comment(var createdBy: String, var content:String, var timeCreated: Timestamp, var createdByCurrent: Boolean? = false, var commentId:String? = null)