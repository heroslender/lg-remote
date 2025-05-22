package com.github.heroslender.lgtvcontroller.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tv::class], version = 1, exportSchema = false)
abstract class TvDatabase: RoomDatabase() {

    companion object {
        @Volatile
        private var Instance: TvDatabase? = null

        fun getDatabase(context: Context): TvDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TvDatabase::class.java, "tv_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }

    abstract fun tvDao(): TvDao
}