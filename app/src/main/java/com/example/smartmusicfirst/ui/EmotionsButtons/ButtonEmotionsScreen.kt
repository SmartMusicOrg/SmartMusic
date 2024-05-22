package com.example.smartmusicfirst.ui.EmotionsButtons

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.accessToken
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.playPlaylist


class ButtonEmotionsScreen {
    @Composable
    fun SimpleButton(text: String, color: Color, modifier: Modifier = Modifier) {
        Button(
            onClick = {
                SpotifyWebApi.searchForPlaylist( "$text mood", accessToken) { playlistId ->
                    if (playlistId.isNotEmpty()) {
                        playPlaylist(playlistId)
                    } else {
                        Log.e(TAG, "No playlist found")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = color),
            modifier = modifier.width(300.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(1.dp)
            )
        }
    }

    @Composable
    fun MySimpleAppContainer(modifier: Modifier = Modifier) {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            SimpleButton("Happy", color = Color.Green)
            SimpleButton("Excited", color = Color(0xFFFFD151))
            SimpleButton("Angry",  color = Color.Red)
            SimpleButton("Optimistic", color = Color.Blue)
            SimpleButton("Sad",  color = Color.Gray)
            SimpleButton("Energized", color = Color.Cyan)
        }
    }
}