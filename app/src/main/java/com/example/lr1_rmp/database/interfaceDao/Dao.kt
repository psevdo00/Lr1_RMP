package com.example.lr1_rmp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {

    @Insert
    fun insertUser(user: UserEntity)

    @Query("Select * from users")
    fun getAllUser(): List<UserEntity>

    @Query("Select exists(select * from users where user_login = :login)")
    fun getUserByLogin(login: String): Boolean

}