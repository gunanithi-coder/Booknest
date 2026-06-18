package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val passwordHash: String, // simple hashed / store plain for secure prototype auth
    val city: String,
    val phone: String,
    val bio: String = "Book lover and avid collector.",
    val rating: Double = 5.0,
    val joinDate: Long = System.currentTimeMillis()
)
