package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartmusicfirst.R

@Composable
fun LogInScreen(onLoginClick: () -> Unit, errorMessage: String? = null) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.fall_login),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
            ) {
                Text(
                    text = "Welcome to Smart Music!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_large)))
                Image(
                    painter = painterResource(id = R.drawable.music_note),
                    contentDescription = "big icon",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.image_size_large))
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_large)))
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFF795548), // Brown color
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Log in with Spotify",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                    )
                }
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_large)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Spotify authentication failed:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LogInScreenPreview() {
    LogInScreen(onLoginClick = {})
}
