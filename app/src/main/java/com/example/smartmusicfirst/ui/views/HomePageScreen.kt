package com.example.smartmusicfirst.ui.views

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun HomePageScreen(
    onNavigateToEmotionButtons: () -> Unit,
    onNavigateToTextCapturing: () -> Unit,
    onNavigateToImageCapturing: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item{
            Text(
                text = stringResource(id = R.string.greeting, SpotifyWebApi.currentUser.displayName),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
        item {
            ScreenCard(
                onClick = onNavigateToEmotionButtons,
                title = R.string.emotionButtonsPage_title,
                bodyText = R.string.emotionButtonsPage_description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
        item {
            ScreenCard(
                onClick = onNavigateToTextCapturing,
                title = R.string.textCapturingPage_title,
                bodyText = R.string.textCapturingPage_description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
        item {
            ScreenCard(
                onClick = onNavigateToImageCapturing,
                title = R.string.imageCapturingPage_title,
                bodyText = R.string.imageCapturingPage_description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
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
            .padding(dimensionResource(id = R.dimen.padding_small))
    ) {
        // TODO add image
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_extra_small))
        ) {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
            Text(
                text = stringResource(id = bodyText),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
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
