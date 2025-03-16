package com.example.lr1_rmp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "user_login")
    val login: String,

    @ColumnInfo(name = "user_email")
    val email: String,

    @ColumnInfo(name = "user_pass")
    val password: String

)
