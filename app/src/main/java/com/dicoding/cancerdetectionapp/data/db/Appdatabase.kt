package com.dicoding.cancerdetectionapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [predictionhistory::class], version = 2, exportSchema = false)
abstract class Appdatabase : RoomDatabase() {

    abstract fun HistoryDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: Appdatabase? = null

        fun getDatabase(context: Context): Appdatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Appdatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}