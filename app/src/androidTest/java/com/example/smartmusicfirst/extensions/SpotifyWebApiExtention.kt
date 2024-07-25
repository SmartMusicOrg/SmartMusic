package com.example.smartmusicfirst.extensions

import android.content.Context
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.models.SpotifyArtist
import com.example.smartmusicfirst.models.SpotifyUser

fun SpotifyWebApi.testInit(applicationContext: Context) {
    this.favoriteArtists = listOf(
        SpotifyArtist(
            uri = "spotify:artist:1",
            name = "Artist 1",
            genres = listOf("genre1", "genre2")
        ),
        SpotifyArtist(
            uri = "spotify:artist:2",
            name = "Artist 2",
            genres = listOf("genre3", "genre4")
        )
    )
    this.accessToken = "something"
    this.applicationContext = applicationContext
    this.currentUser = SpotifyUser(
        uri = "spotify:user:123456789",
        id = "123456789",
        displayName = "Test User",
        images = listOf("https://example.com/image.jpg"),
        product = "premium"
    )
}