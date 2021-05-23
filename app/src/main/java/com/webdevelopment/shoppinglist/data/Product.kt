package com.webdevelopment.shoppinglist.data

import com.google.firebase.firestore.Exclude

data class Product (var title: String = "", var quantity: Int = 0, @get:Exclude var isChecked: Boolean = false, @get:Exclude var id: String = "")
