package com.dicoding.asclepius.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Result::class], version = 1, exportSchema = false)
abstract class ResultDatabase : RoomDatabase() {
    abstract fun resultDao(): ResultDao

    companion object {
        @Volatile
        private var INSTANCE: ResultDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ResultDatabase {
            if (INSTANCE == null) {
                synchronized(ResultDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ResultDatabase::class.java,
                        "result_database"
                    ).build()
                }
            }
            return INSTANCE as ResultDatabase
        }
    }
}