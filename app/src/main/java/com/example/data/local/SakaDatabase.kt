package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.PracticeSession
import com.example.data.model.SavedTopicEntity

@Database(entities = [PracticeSession::class, SavedTopicEntity::class], version = 1, exportSchema = false)
abstract class SakaDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao

    companion object {
        @Volatile
        private var INSTANCE: SakaDatabase? = null

        fun getDatabase(context: Context): SakaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SakaDatabase::class.java,
                    "saka_speak_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
