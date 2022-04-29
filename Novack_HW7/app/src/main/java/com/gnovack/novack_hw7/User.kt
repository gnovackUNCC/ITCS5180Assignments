package com.gnovack.novack_hw7

data class User(val id:String? = null, val name:String? = null, val ownedLists:List<String>? = listOf(), val sharedLists:List<String>? = listOf())


