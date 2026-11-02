package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_topics")
data class SavedTopicEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val title: String,
    val quote: String,
    val pointsText: String, // newline-separated
    val perspectivesText: String, // newline-separated
    val challenge: String,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false
) {
    fun toTopic(): Topic {
        return Topic(
            id = id,
            category = category,
            title = title,
            quote = quote,
            points = pointsText.split("\n").filter { it.isNotBlank() },
            perspectives = perspectivesText.split("\n").filter { it.isNotBlank() },
            challenge = challenge,
            isFavorite = isFavorite,
            isCustom = isCustom
        )
    }

    companion object {
        fun fromTopic(topic: Topic): SavedTopicEntity {
            return SavedTopicEntity(
                id = topic.id,
                category = topic.category,
                title = topic.title,
                quote = topic.quote,
                pointsText = topic.points.joinToString("\n"),
                perspectivesText = topic.perspectives.joinToString("\n"),
                challenge = topic.challenge,
                isCustom = topic.isCustom,
                isFavorite = topic.isFavorite
            )
        }
    }
}
