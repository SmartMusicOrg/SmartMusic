//package com.example.smartmusicfirst
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnection
//import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnectionListener
//import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
//import com.example.smartmusicfirst.connectors.spotify.SpotifyConnectionListener
//import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
//
//class LogInActivity : ComponentActivity(), SpotifyConnectionListener,
//    SpotifyAuthConnectionListener {
//
//    private val spotifyAuthLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            SpotifyAuthConnection.onActivityResult(result.resultCode, result.data)
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            Surface(
//                modifier = Modifier.fillMaxSize(),
//                color = MaterialTheme.colorScheme.background
//            ) {
//                LogInScreen(::onLoginButtonClick)
//            }
//        }
//    }
//
//    private fun onLoginButtonClick() {
//        SpotifyAuthConnection.initAuthConnection(this, this, spotifyAuthLauncher)
//        SpotifyWebApi.init(applicationContext)
//    }
//
//    override fun onSpotifyConnected() {
//        Log.d(TAG, "Spotify connected")
//    }
//
//    override fun onSpotifyAuthSuccess(accessToken: String) {
//        Log.d(TAG, "Spotify authentication successful, Access Token: $accessToken")
//        com.example.smartmusicfirst.accessToken = accessToken
//        SpotifyConnection.connect(this, this)
//        startActivity(Intent(this, MainActivity::class.java))
//        finish() // Close the login activity
//    }
//
//    override fun onSpotifyAuthFailure(error: Throwable) {
//        Log.e(TAG, "Spotify authentication failed: ${error.message}", error)
//        setContent {
//            Surface(
//                modifier = Modifier.fillMaxSize(),
//                color = MaterialTheme.colorScheme.background
//            ) {
//                Box(modifier = Modifier.fillMaxSize()) {
//                    LogInScreen(::onLoginButtonClick)
//                    Box(
//                        contentAlignment = Alignment.BottomCenter,
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        Column {
//                            Text(
//                                text = "Spotify authentication failed:",
//                                style = MaterialTheme.typography.titleSmall,
//                                color = MaterialTheme.colorScheme.error
//                            )
//                            Text(
//                                text = error.message ?: "Unknown error",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.error
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun LogInScreen(onLoginClick: () -> Unit) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        //todo put in string xml:
//        Text(
//            text = "Welcome to Smart Music!",
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.displayLarge,
//            color = MaterialTheme.colorScheme.primary
//        )
//        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_large)))
//        Box(
//            modifier = Modifier
//                .height(dimensionResource(id = R.dimen.height_large))
//                .fillMaxSize()
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.home_icon),
//                contentDescription = "big icon",
//                contentScale = ContentScale.FillHeight,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_large)))
//        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_large)))
//        Button(onClick = onLoginClick) {
//            Text(
//                text = "Log in with Spotify", style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun LogInScreenPreview() {
//    LogInScreen {}
//}

package com.example.smartmusicfirst

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.ui.views.LogInScreen

class LogInActivity : ComponentActivity(), SpotifyConnectionListener, SpotifyAuthConnectionListener {

    private val spotifyAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            SpotifyAuthConnection.onActivityResult(result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogInScreen(::onLoginButtonClick)
        }
    }

    private fun onLoginButtonClick() {
        SpotifyAuthConnection.initAuthConnection(this, this, spotifyAuthLauncher)
        SpotifyWebApi.init(applicationContext)
    }

    override fun onSpotifyConnected() {
        Log.d(TAG, "Spotify connected")
    }

    override fun onSpotifyAuthSuccess(accessToken: String) {
        Log.d(TAG, "Spotify authentication successful, Access Token: $accessToken")
        com.example.smartmusicfirst.accessToken = accessToken
        SpotifyConnection.connect(this, this)
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Close the login activity
    }

    override fun onSpotifyAuthFailure(error: Throwable) {
        Log.e(TAG, "Spotify authentication failed: ${error.message}", error)
        setContent {
            LogInScreen(::onLoginButtonClick, error.message ?: "Unknown error")
        }
    }

    companion object {
        private const val TAG = "LogInActivity"
    }
}

@Preview
@Composable
fun LogInScreenPreview() {
    LogInScreen({})
}