package com.gnovack.novack_hw7

import java.io.Serializable

data class ShoppingList(var id:String? = null, val name:String? = null, val owner:String? = null, val items:List<ShoppingItem>? = listOf(), var createdByCurrent: Boolean? = false, val sharedWith: List<String> = listOf()): Serializable
