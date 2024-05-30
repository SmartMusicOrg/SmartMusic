package com.example.smartmusicfirst.models

data class SpotifySong(
    val uri: String,
    val name: String,
    val artistsUri: List<String>? = null,
    val album: String? = null,
    val imageUrl: String? = null,
    val popularity: Int? = null,
)
