package com.example.lr1_rmp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lr1_rmp.database.interfaceDao.Dao
import com.example.lr1_rmp.database.entities.UserEntity

@Database(

    version = 1,
    entities = [UserEntity::class]

)
abstract class MainDb : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {

        fun getDb(context: Context): MainDb {

            return Room.databaseBuilder(context.applicationContext, MainDb::class.java, name = "users").build()

        }

    }

}