package com.example.smartmusicfirst.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.accessToken
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.playPlaylist
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme


@Composable
fun SimpleButton(text: String, color: Color, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = {
            SpotifyWebApi.searchForPlaylist("$text mood", accessToken) { playlistId ->
                if (playlistId.isNotEmpty()) {
                    playPlaylist(playlistId)
                } else {
                    Log.e(TAG, "No playlist found")
                }
            }
        },
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 3.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier.width(300.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(1.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun MySimpleAppContainer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SimpleButton("Happy", color = Color.Green)
        SimpleButton("Excited", color = Color(0xFFFFD151))
        SimpleButton("Angry", color = Color.Red)
        SimpleButton("Optimistic", color = Color.Blue)
        SimpleButton("Sad", color = Color.DarkGray)
        SimpleButton("Energized", color = Color.Cyan)
    }
}

@Preview
@Composable
fun ButtonEmotionsScreenPreview() {
    SmartMusicFirstTheme {
        MySimpleAppContainer(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
fun ButtonEmotionsScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        MySimpleAppContainer(modifier = Modifier.fillMaxSize())
    }
}
