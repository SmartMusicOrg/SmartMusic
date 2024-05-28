package com.example.smartmusicfirst.ui.views

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun HomePageScreen(
    onNavigateToEmotionButtons: () -> Unit,
    onNavigateToTextCapturing: () -> Unit,
    onNavigateToImageCapturing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        ScreenCard(
            onClick = onNavigateToEmotionButtons,
            title = R.string.emotionButtonsPage_title,
            bodyText = R.string.emotionButtonsPage_description
        )
        ScreenCard(
            onClick = onNavigateToTextCapturing,
            title = R.string.textCapturingPage_title,
            bodyText = R.string.textCapturingPage_description
        )
        ScreenCard(
            onClick = onNavigateToImageCapturing,
            title = R.string.imageCapturingPage_title,
            bodyText = R.string.imageCapturingPage_description
        )
    }
}

@Composable
fun ScreenCard(
    onClick: () -> Unit,
    @StringRes title: Int,
    @StringRes bodyText: Int,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // TODO add image
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(id = bodyText),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

}

@Preview
@Composable
fun HomePageScreenPreview() {
    SmartMusicFirstTheme {
        HomePageScreen(
            onNavigateToEmotionButtons = { },
            onNavigateToTextCapturing = { },
            onNavigateToImageCapturing = { },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun HomePageScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        HomePageScreen(
            onNavigateToEmotionButtons = { },
            onNavigateToTextCapturing = { },
            onNavigateToImageCapturing = { },
            modifier = Modifier.fillMaxSize()
        )
    }
}
