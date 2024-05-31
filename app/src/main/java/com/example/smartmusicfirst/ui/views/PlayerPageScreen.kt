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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.models.SpotifySong
import com.example.smartmusicfirst.playSong
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun PlayerPageScreen(songPlaylist: List<SpotifySong>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            items(songPlaylist.size) { index ->
                SpotifySongItemView(songPlaylist[index])
                HorizontalDivider()
            }
        }
        Column {
            PlayerControllerView()
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_medium)))
        }
    }
}

@Composable
fun SpotifySongItemView(spotifySong: SpotifySong) {
    ListItem(
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.music_note),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.height_medium))
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
        },
        headlineContent = {
            Text(text = spotifySong.name)
        },
        supportingContent = {
            Text(text = spotifySong.artistsUri?.joinToString(", ") ?: "")
        },
        tonalElevation = dimensionResource(id = R.dimen.elevation_small),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { playSong(spotifySong.uri) }
    )
}

//@OptIn(ExperimentalCoilApi::class)
@Composable
fun PlayerControllerView() {
    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.height_extra_large))
            .width(300.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
        ) {
            ButtonControllerView(icon = R.drawable.skip_previous) {/*TODO: skip previous*/ }
            ButtonControllerView(icon = R.drawable.play) {/*TODO: play/pause*/ }
            ButtonControllerView(icon = R.drawable.skip_next) {/*TODO: skip next*/ }
        }
    }
}

@Composable
fun ButtonControllerView(@DrawableRes icon: Int, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.height_large))
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "previous song",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
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
            songPlaylist = listOf(
                SpotifySong(
                    uri = "spotify:track:4uX1pkSuSidzJxT4eWL7x1",
                    name = "Good Mood - Original Song From Paw Patrol: The Movie",
                    artistsUri = listOf("spotify:artist:4bYPcJP5jwMhSivRcqie2n"),
                    album = "Good Mood (Original Song From Paw Patrol: The Movie)",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),
                SpotifySong(
                    uri = "spotify:album:0KeNJa8ky2LAuzKjUqz6EK",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:2KDWVRwZ75kQJ0HoqdSjpi"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d00004851f580861a26c216beecdbba5c"
                ),
                SpotifySong(
                    uri = "spotify:track:4cbE108aX1pnOAPf2xyK2q",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:0frA7bPCcyvwEKpcKD6NnJ"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),
                SpotifySong(
                    uri = "spotify:track:4uX1pkSuSidzJxT4eWL7x1",
                    name = "Good Mood - Original Song From Paw Patrol: The Movie",
                    artistsUri = listOf("spotify:artist:4bYPcJP5jwMhSivRcqie2n"),
                    album = "Good Mood (Original Song From Paw Patrol: The Movie)",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),
                SpotifySong(
                    uri = "spotify:album:0KeNJa8ky2LAuzKjUqz6EK",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:2KDWVRwZ75kQJ0HoqdSjpi"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d00004851f580861a26c216beecdbba5c"
                ),
                SpotifySong(
                    uri = "spotify:track:4cbE108aX1pnOAPf2xyK2q",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:0frA7bPCcyvwEKpcKD6NnJ"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),                SpotifySong(
                    uri = "spotify:track:4uX1pkSuSidzJxT4eWL7x1",
                    name = "Good Mood - Original Song From Paw Patrol: The Movie",
                    artistsUri = listOf("spotify:artist:4bYPcJP5jwMhSivRcqie2n"),
                    album = "Good Mood (Original Song From Paw Patrol: The Movie)",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),
                SpotifySong(
                    uri = "spotify:album:0KeNJa8ky2LAuzKjUqz6EK",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:2KDWVRwZ75kQJ0HoqdSjpi"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d00004851f580861a26c216beecdbba5c"
                ),
                SpotifySong(
                    uri = "spotify:track:4cbE108aX1pnOAPf2xyK2q",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:0frA7bPCcyvwEKpcKD6NnJ"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),                SpotifySong(
                    uri = "spotify:track:4uX1pkSuSidzJxT4eWL7x1",
                    name = "Good Mood - Original Song From Paw Patrol: The Movie",
                    artistsUri = listOf("spotify:artist:4bYPcJP5jwMhSivRcqie2n"),
                    album = "Good Mood (Original Song From Paw Patrol: The Movie)",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),
                SpotifySong(
                    uri = "spotify:album:0KeNJa8ky2LAuzKjUqz6EK",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:2KDWVRwZ75kQJ0HoqdSjpi"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d00004851f580861a26c216beecdbba5c"
                ),
                SpotifySong(
                    uri = "spotify:track:4cbE108aX1pnOAPf2xyK2q",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:0frA7bPCcyvwEKpcKD6NnJ"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun PlayerPageScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        PlayerPageScreen(
            songPlaylist = listOf(
                SpotifySong(
                    uri = "spotify:track:4uX1pkSuSidzJxT4eWL7x1",
                    name = "Good Mood - Original Song From Paw Patrol: The Movie",
                    artistsUri = listOf("spotify:artist:4bYPcJP5jwMhSivRcqie2n"),
                    album = "Good Mood (Original Song From Paw Patrol: The Movie)",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                ),
                SpotifySong(
                    uri = "spotify:album:0KeNJa8ky2LAuzKjUqz6EK",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:2KDWVRwZ75kQJ0HoqdSjpi"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d00004851f580861a26c216beecdbba5c"
                ),
                SpotifySong(
                    uri = "spotify:track:4cbE108aX1pnOAPf2xyK2q",
                    name = "Happy Mood",
                    artistsUri = listOf("spotify:artist:0frA7bPCcyvwEKpcKD6NnJ"),
                    album = "Happy Mood",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000485198ac1936c5f9a9d0911754b2"
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}
