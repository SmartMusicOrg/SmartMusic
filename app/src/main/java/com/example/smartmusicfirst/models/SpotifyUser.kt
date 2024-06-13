package com.example.smartmusicfirst.models

data class SpotifyUser(
    val uri: String,
    val id: String,
    val displayName: String,
    val images: List<String>,
    val product: String  // Spotify subscription level "premium" or "free"
)
