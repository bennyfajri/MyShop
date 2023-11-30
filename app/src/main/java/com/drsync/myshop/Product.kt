package com.drsync.myshop

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    var qty: Int? = 0,
    var totalPrice: Int? = 0,
    var customer: String? = ""
)