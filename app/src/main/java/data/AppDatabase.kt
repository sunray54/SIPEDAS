package com.hafizhihiman.sipedas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DiagnosisRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisDao(): DiagnosisDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sipedas_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}