package com.example.data.model

data class Topic(
    val id: String,
    val category: String,
    val title: String,
    val quote: String,
    val points: List<String>,
    val perspectives: List<String>,
    val challenge: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
