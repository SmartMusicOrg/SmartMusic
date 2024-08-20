package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.spotify_profie_data),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_large))
            )
            Text(
                text = stringResource(
                    id = R.string.spotify_user_name,
                    SpotifyWebApi.currentUser.displayName
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_medium))
            )
            Text(
                text = stringResource(
                    id = R.string.spotify_product,
                    SpotifyWebApi.currentUser.product
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_medium))
            )
            Text(
                text = stringResource(id = R.string.favorite_artists_list),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_medium))
            )
            SpotifyWebApi.favoriteArtists.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.padding_small)),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()
                            .padding(
                                start = dimensionResource(id = R.dimen.padding_medium),
                            )
                    )
                }
            }

        }

    }
}
//        item {
//            Text("Settings", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(bottom = 16.dp))
//        }
//
//        item {
//            Text("Change Email", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 8.dp))
//            BasicTextField(
//                value = uiState.email,
//                onValueChange = { viewModel.updateEmail(it) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//                    .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
//                    .padding(8.dp)
//            )
//        }
//
//        item {
//            SettingItem("Enable Notifications") {
//                Switch(
//                    checked = uiState.notificationsEnabled,
//                    onCheckedChange = { viewModel.updateNotificationsEnabled(it) }
//                )
//            }
//        }
//
//        item {
//            SettingItem("Enable Offline Playlists") {
//                Switch(
//                    checked = uiState.offlinePlaylistsEnabled,
//                    onCheckedChange = { viewModel.updateOfflinePlaylistsEnabled(it) }
//                )
//            }
//        }
//
//        item {
//            Text("Select Your Preferred Genres", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 16.dp))
//        }
//
//        item {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                elevation = CardDefaults.cardElevation(4.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    GenreCheckbox("Rock", uiState.rockSelected) { viewModel.updateRockSelected(it) }
//                    GenreCheckbox("Pop", uiState.popSelected) { viewModel.updatePopSelected(it) }
//                    GenreCheckbox("Jazz", uiState.jazzSelected) { viewModel.updateJazzSelected(it) }
//                    GenreCheckbox("Classical", uiState.classicalSelected) { viewModel.updateClassicalSelected(it) }
//                    GenreCheckbox("Hip-Hop", uiState.hipHopSelected) { viewModel.updateHipHopSelected(it) }
//                }
//            }
//        }
//
//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        item {
//            Button(
//                onClick = { /* Save preferences action */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//            ) {
//                Text("Save Preferences")
//            }
//        }
//    }
//}
//
//@Composable
//fun SettingItem(title: String, content: @Composable () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
//        content()
//    }
//}
//
//@Composable
//fun GenreCheckbox(genre: String, isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//    ) {
//        Checkbox(
//            checked = isSelected,
//            onCheckedChange = onSelectedChange,
//            colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary)
//        )
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(genre, style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
//    }
//}

@Preview(showBackground = true)
@Composable
fun PreferencesScreenPreview() {
    SettingsScreen()
}
