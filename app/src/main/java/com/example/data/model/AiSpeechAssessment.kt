package com.example.data.model

data class AiSpeechAssessment(
    val overallScore: Int,
    val predikat: String,
    val skorBahasa: Int,
    val catatanBahasa: String,
    val skorArtikulasi: Int,
    val catatanArtikulasi: String,
    val skorNada: Int,
    val catatanNada: String,
    val skorImprovisasi: Int,
    val catatanImprovisasi: String,
    val skorWawasan: Int,
    val catatanWawasan: String,
    val kelebihan: List<String>,
    val areaPerbaikan: List<String>,
    val saranLatihan: List<String>,
    val ringkasanEksekutif: String
)
