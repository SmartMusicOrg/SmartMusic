package com.example.smartmusicfirst.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun ImageCapturingScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    onCaptureClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF)) // Background color
            .padding(horizontal = 24.dp, vertical = 32.dp), // Increased padding for better spacing
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 32.sp,
            color = Color(0xFFFFC107), // Title text color
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp) // Bottom padding for spacing
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFBB86FC), // Message text color
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp) // Bottom padding for spacing
        )

        IconButton(
            onClick = {
                // Trigger image capture functionality
                Log.d("ImageCapturingScreen", "Capture icon clicked")
                onCaptureClick()
            },
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFBB86FC), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Capture Image",
                tint = Color.White
            )
        }
    }
}

@Preview
@Composable
fun ImageCapturingScreenPreview() {
    SmartMusicFirstTheme {
        ImageCapturingScreen(
            title = "Capture Image",
            message = "Tap the button below to capture an image.",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun ImageCapturingScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        ImageCapturingScreen(
            title = "Capture Image",
            message = "Tap the button below to capture an image.",
            modifier = Modifier.fillMaxSize()
        )
    }
}