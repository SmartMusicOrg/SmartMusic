package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.models.SettingsOnOffItem
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun SettingsScreen(
    settingsItemOnOffList: List<SettingsOnOffItem>,
    message: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        items(settingsItemOnOffList.size) { index ->
            SettingsOnOffItemView(settingsItemOnOffList[index])
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SettingsOnOffItemView(settingsOnOffItem: SettingsOnOffItem) {
    Card(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
        Row (modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = settingsOnOffItem.title)
                Text(text = settingsOnOffItem.description)
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = settingsOnOffItem.isChecked,
                onCheckedChange = { settingsOnOffItem.isChecked = !settingsOnOffItem.isChecked },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )

        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SmartMusicFirstTheme {
        SettingsScreen(
            settingsItemOnOffList = listOf(
                SettingsOnOffItem("Item 1", true, "Description 1"),
                SettingsOnOffItem("Item 2", false, "Description 2"),
                SettingsOnOffItem("Item 3", true, "Description 3"),
                SettingsOnOffItem("Item 4", false, "Description 4"),
                SettingsOnOffItem("Item 5", true, "Description 5"),
                SettingsOnOffItem("Item 6", false, "Description 6"),
                SettingsOnOffItem("Item 7", true, "Description 7"),
                SettingsOnOffItem("Item 8", false, "Description 8"),
                SettingsOnOffItem("Item 9", true, "Description 9"),
                SettingsOnOffItem("Item 10", false, "Description 10")
            ),
            message = "Some example of message Lorem Ipsum Dolor sit ...",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun SettingsScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        SettingsScreen(
            settingsItemOnOffList = listOf(
                SettingsOnOffItem("Item 1", true, "Description 1"),
                SettingsOnOffItem("Item 2", false, "Description 2"),
                SettingsOnOffItem("Item 3", true, "Description 3"),
                SettingsOnOffItem("Item 4", false, "Description 4"),
                SettingsOnOffItem("Item 5", true, "Description 5"),
                SettingsOnOffItem("Item 6", false, "Description 6"),
                SettingsOnOffItem("Item 7", true, "Description 7"),
                SettingsOnOffItem("Item 8", false, "Description 8"),
                SettingsOnOffItem("Item 9", true, "Description 9"),
                SettingsOnOffItem("Item 10", false, "Description 10")
            ),
            message = "Some example of message Lorem Ipsum Dolor sit ...",
            modifier = Modifier.fillMaxSize()
        )
    }
}
