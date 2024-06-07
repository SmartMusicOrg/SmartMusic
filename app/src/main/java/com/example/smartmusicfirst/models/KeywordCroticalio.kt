package com.example.smartmusicfirst.models

data class KeywordCroticalio(
    val word: String,
    val documentFrequency: Double,
    val posTags: List<String>,
    val score: Double
)
