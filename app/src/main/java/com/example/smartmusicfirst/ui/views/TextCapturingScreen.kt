package com.example.smartmusicfirst.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme
import com.example.smartmusicfirst.viewModels.TextCapturingViewModel
import java.util.Properties

@Composable
fun TextCapturingScreen(
    modifier: Modifier = Modifier,
    textCapturingViewModel: TextCapturingViewModel = viewModel()
) {
    val uiState by textCapturingViewModel.uiState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium),
                vertical = dimensionResource(id = R.dimen.padding_large)
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Search for a song",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_medium))
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = dimensionResource(id = R.dimen.padding_medium))
        ) {
            IconButton(
                onClick = {
                    textCapturingViewModel.speechToText(context)
                    Log.d(TAG, "Microphone icon clicked")
                },
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.height_large))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Microphone",
                    tint = Color.White
                )
            }
            TextField(
                value = uiState.inputString,
                onValueChange = { textCapturingViewModel.updateInputString(it) },
                placeholder = { Text("Express your feelings right now") },
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = dimensionResource(id = R.dimen.padding_small)) // Added padding for better spacing
            )
        }

        Button(
            onClick = {
                val properties = Properties().apply { load(context.resources.openRawResource(R.raw.corticalio)) }
                val corticalioAccessToken = properties.getProperty("croticalio_access_token") ?: ""
                properties.load(context.resources.openRawResource(R.raw.gemini))
                val geminiApiKey = properties.getProperty("gemini_api_key") ?: ""
                textCapturingViewModel.searchSong(corticalioAccessToken, geminiApiKey)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(dimensionResource(id = R.dimen.height_large)),
        ) {
            Text(
                text = "Search",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun SongSearchScreenPreview() {
    SmartMusicFirstTheme {
        TextCapturingScreen()
    }
}

@Preview
@Composable
fun SongSearchScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        TextCapturingScreen()
    }
}
