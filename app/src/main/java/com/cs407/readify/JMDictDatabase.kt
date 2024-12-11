package com.cs407.readify

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JMDictEntry::class], version = 1, exportSchema = false)
abstract class JMDictDatabase : RoomDatabase() {
    abstract fun jmdictDao(): JMDictDao

    companion object {
        @Volatile
        private var INSTANCE: JMDictDatabase? = null

        fun getDatabase(context: Context): JMDictDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JMDictDatabase::class.java,
                    "jmdict.db"
                )
                    // If you have a prebuilt DB in assets/databases/jmdict.db
                    .createFromAsset("databases/jmdict.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}