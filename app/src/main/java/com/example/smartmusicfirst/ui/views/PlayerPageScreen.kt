package com.example.smartmusicfirst.ui.views

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme
import com.example.smartmusicfirst.viewModels.PlayerPageViewModel

@Composable
fun PlayerPageScreen(
    modifier: Modifier = Modifier,
    playerPageViewModel: PlayerPageViewModel = viewModel()
) {
    val uiState by playerPageViewModel.uiState.collectAsState()
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Enjoy your music!",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_medium)))
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "music",
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.onBackground)
                    .padding(dimensionResource(id = R.dimen.padding_extra_large))
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_medium)))
            PlayerControllerView(
                isPlaying = uiState.isPlaying,
                playToggle = { playerPageViewModel.togglePlay() },
                isPremium = playerPageViewModel.isUserPremium()
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_medium)))
            if (!playerPageViewModel.isUserPremium())
                Text(
                    text = stringResource(id = R.string.upgrade_spotify),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_medium)))
        }
    }
}

@Composable
fun PlayerControllerView(
    isPlaying: Boolean = true,
    playToggle: () -> Unit = {},
    isPremium: Boolean = false
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.height_extra_large))
            .width(300.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            ButtonControllerView(icon = R.drawable.skip_previous, enabled = isPremium) {
                SpotifyConnection.getPlayerApi()!!.skipPrevious()
            }
            ButtonControllerView(icon = if (isPlaying) R.drawable.pause else R.drawable.play) { playToggle() }
            ButtonControllerView(icon = R.drawable.skip_next, enabled = isPremium) {
                SpotifyConnection.getPlayerApi()!!.skipNext()
            }
        }
    }
}

@Composable
fun ButtonControllerView(
    @DrawableRes icon: Int,
    enabled: Boolean = true,
    onClick: () -> Unit = { },
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.height_large))
            .background(
                color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.5f
                ),
                shape = MaterialTheme.shapes.extraLarge
            )
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "playback control button",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.height_medium))

        )

    }

}

@Preview
@Composable
fun PlayerPageScreenPreview() {
    SmartMusicFirstTheme {
        PlayerPageScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun PlayerPageScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        PlayerPageScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}
