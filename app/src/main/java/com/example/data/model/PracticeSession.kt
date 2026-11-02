package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topicTitle: String,
    val category: String,
    val targetSeconds: Int,
    val actualSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val audioFilePath: String? = null,
    val ratingStructure: Int = 4,
    val ratingClarity: Int = 4,
    val ratingPacing: Int = 4,
    val ratingConfidence: Int = 4,
    val notes: String = "",
    val aiOverallScore: Int? = null,
    val aiScoreBahasa: Int? = null,
    val aiScoreArtikulasi: Int? = null,
    val aiScoreNada: Int? = null,
    val aiScoreImprovisasi: Int? = null,
    val aiScoreWawasan: Int? = null,
    val aiFeedbackSummary: String? = null
)
