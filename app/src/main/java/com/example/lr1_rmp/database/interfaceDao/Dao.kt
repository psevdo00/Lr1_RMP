package com.example.lr1_rmp.database.interfaceDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lr1_rmp.database.entities.UserEntity
import com.example.lr1_rmp.database.resultQuery.ResultQueryGetUserByLogin

@Dao
interface Dao {

    @Insert
    fun insertUser(user: UserEntity)

    @Query("Select * from users")
    fun getAllUser(): List<UserEntity>

    @Query("Select user_login, user_pass from users where user_login = :login")
    fun getUserByLogin(login: String): ResultQueryGetUserByLogin?

}