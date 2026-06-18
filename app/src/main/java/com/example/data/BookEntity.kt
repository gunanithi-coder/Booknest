package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val isbn: String,
    val category: String,
    val language: String,
    val description: String,
    val condition: String,
    val price: Double,
    val coverGradientIndex: Int = 0, // Selection of beautiful gradients for native rendering
    val coverColorHex: String = "#8B4513", // Primary background color for book cover
    val sellerId: Int,
    val sellerName: String,
    val sellerEmail: String,
    val sellerPhone: String = "",
    val city: String,
    val status: String = "ACTIVE", // ACTIVE or SOLD
    val createdAt: Long = System.currentTimeMillis()
)
