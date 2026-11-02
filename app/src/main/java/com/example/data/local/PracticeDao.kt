package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PracticeSession
import com.example.data.model.SavedTopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {
    // Practice Sessions
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession): Long

    @Delete
    suspend fun deleteSession(session: PracticeSession)

    @Query("DELETE FROM practice_sessions")
    suspend fun deleteAllSessions()

    // Saved Topics (Favorites & Custom)
    @Query("SELECT * FROM saved_topics")
    fun getAllSavedTopics(): Flow<List<SavedTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedTopic(topic: SavedTopicEntity)

    @Update
    suspend fun updateSavedTopic(topic: SavedTopicEntity)

    @Query("DELETE FROM saved_topics WHERE id = :id")
    suspend fun deleteSavedTopic(id: String)
}
