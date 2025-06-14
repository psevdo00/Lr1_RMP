package com.example.lr1_rmp.database.interfaceDao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserNameDAO {

    @Query("Select id from users where user_login = :login")
    suspend fun getIdByLogin(login: String): Int

}