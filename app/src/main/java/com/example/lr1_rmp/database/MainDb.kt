package com.example.lr1_rmp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(

    version = 1,
    entities = [UserEntity::class]

)
abstract class MainDb : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {

        fun getDb(context: Context): MainDb{

            return Room.databaseBuilder(context.applicationContext, MainDb::class.java, name = "users").build()

        }

    }

}