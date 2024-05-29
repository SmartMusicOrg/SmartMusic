package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun ContactUsScreen(title: String, message: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color =MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color =MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
fun ContactUsScreenPreview() {
    SmartMusicFirstTheme {
        ContactUsScreen(
            title = "Example Title",
            message = "Some example of message Lorem Ipsum Dolor sit ...",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun ContactUsScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        ContactUsScreen(
            title = "Example Title",
            message = "Some example of message Lorem Ipsum Dolor sit ...",
            modifier = Modifier.fillMaxSize()
        )
    }
}
