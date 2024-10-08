package com.example.smartmusicfirst.ui.views

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.connectors.ai.ChatGptApi
import com.example.smartmusicfirst.ui.components.LoadingPage
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme
import com.example.smartmusicfirst.viewModels.TextCapturingViewModel
import java.util.Properties

@Composable
fun TextCapturingScreen(
    onNavigateToPlayerPage: () -> Unit,
    modifier: Modifier = Modifier,
    textCapturingViewModel: TextCapturingViewModel = viewModel()
) {
    val uiState by textCapturingViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val recordAudioLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            textCapturingViewModel.enableRecording(isGranted)
        }

    LaunchedEffect(key1 = recordAudioLauncher) {
        recordAudioLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(key1 = uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty())
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
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
                text = stringResource(id = R.string.search_for_playlist),
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
                    enabled = uiState.canUseRecord && uiState.recordingGranted,
                    onClick = { textCapturingViewModel.speechToTextButtonClicked() },
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.height_large))
                        .clip(CircleShape)
                        .background(
                            if (!uiState.canUseRecord || !uiState.recordingGranted)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            else
                                if (uiState.isListening)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mic_icon),
                        contentDescription = "Mic",
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                TextField(
                    value = uiState.inputString,
                    onValueChange = { textCapturingViewModel.updateInputString(it) },
                    placeholder = { Text(stringResource(id = R.string.text_capture_hint)) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = dimensionResource(id = R.dimen.padding_small)) // Added padding for better spacing
                )
            }

            Button(
                enabled = uiState.canUseSubmit,
                onClick = {
                    val properties =
                        Properties().apply { load(context.resources.openRawResource(R.raw.corticalio)) }
                    val corticalioAccessToken =
                        properties.getProperty("croticalio_access_token") ?: ""
                    properties.load(context.resources.openRawResource(R.raw.gemini))
                    val aiApiKey = properties.getProperty("chat_gpt_access_token") ?: ""
                    textCapturingViewModel.searchSong(
                        corticalioAccessToken,
                        aiApiKey,
                        ChatGptApi
                    ) { onNavigateToPlayerPage() }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(dimensionResource(id = R.dimen.height_large)),
            ) {
                Text(
                    text = stringResource(id = R.string.search),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
        if (uiState.isLoading) {
            LoadingPage(hint = uiState.userHint)
        }
    }
}

@Preview
@Composable
fun SongSearchScreenPreview() {
    SmartMusicFirstTheme {
        TextCapturingScreen({})
    }
}

@Preview
@Composable
fun SongSearchScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        TextCapturingScreen({})
    }
}
